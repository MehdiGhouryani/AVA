import type { CollectionBeforeChangeHook, CollectionBeforeValidateHook } from 'payload'
import { buildDeterministicExport, type ExportRecord } from '../domain/deterministicExport'
import { releaseBlockers } from '../domain/releasePolicy'
import type { AvaUser } from '../domain/roles'

const relationId = (value: unknown): number | string => {
  if (typeof value === 'object' && value && 'id' in value) {
    return (value as { id: number | string }).id
  }
  return value as number | string
}

export const initializeRelease: CollectionBeforeValidateHook = async ({ data, operation, req }) => {
  const user = req.user as unknown as AvaUser
  if (!user?.role) throw new Error('authenticated_role_required')
  const next = data ?? {}
  if (operation === 'create') {
    next.creator = user.id
    next.state = 'draft'
  }
  return next
}

export const protectReleaseWorkflow: CollectionBeforeChangeHook = async ({
  data,
  operation,
  originalDoc,
  req,
}) => {
  const user = req.user as unknown as AvaUser
  if (!user?.role) throw new Error('authenticated_role_required')
  if (operation === 'create') return data

  const from = String(originalDoc.state)
  const to = String(data.state ?? from)
  if (to === from) return data
  if (to === 'published' && from !== 'ready') throw new Error('release_must_be_ready_first')
  if (!['ready', 'published'].includes(to)) return data

  const audioIds = (data.requiredAudio ?? originalDoc.requiredAudio ?? []).map(relationId)
  const contentIds = (data.contentItems ?? originalDoc.contentItems ?? []).map(relationId)
  const [audioDocs, contentDocs] = await Promise.all([
    Promise.all(
      audioIds.map((id: number | string) =>
        req.payload.findByID({ collection: 'audio-assets', id, overrideAccess: true, req }),
      ),
    ),
    Promise.all(
      contentIds.map((id: number | string) =>
        req.payload.findByID({ collection: 'curriculum-items', id, overrideAccess: true, req }),
      ),
    ),
  ])

  const blockers = releaseBlockers({
    actorId: user.id,
    actorRole: user.role,
    audio: audioDocs.map((doc) => ({
      deliveryObjectKey: doc.deliveryObjectKey,
      deliverySha256: doc.deliverySha256,
      deliveryStatus: doc.deliveryStatus,
      status: doc.status,
    })),
    contentStates: contentDocs.map((doc) => String(doc.reviewStatus)),
    creatorId: originalDoc.creator,
    scope: (data.scope ?? originalDoc.scope) as 'complete' | 'partial',
  })
  if (blockers.length) throw new Error(`release_blocked:${blockers.join(',')}`)

  if (to === 'published') {
    const exported = buildDeterministicExport({
      candidateId: String(data.stableId ?? originalDoc.stableId),
      contentVersion: String(data.contentVersion ?? originalDoc.contentVersion),
      content: contentDocs.map((doc) => ({
        body: doc.body,
        kind: doc.kind,
        order: doc.order,
        parentStableId: doc.parentStableId,
        stableId: doc.stableId,
        titleFa: doc.titleFa,
      })) as ExportRecord[],
      audio: audioDocs.map((doc) => ({
        bytes: doc.deliveryBytes,
        mime: doc.deliveryMime,
        objectKey: doc.deliveryObjectKey,
        sha256: doc.deliverySha256,
        stableId: doc.stableId,
      })) as ExportRecord[],
    })
    data.exportPayload = exported.payload
    data.exportSha256 = exported.sha256
    data.publishedAt = new Date().toISOString()
    data.publisher = user.id
  }
  return data
}

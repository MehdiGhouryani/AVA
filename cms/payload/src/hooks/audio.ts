import type { CollectionBeforeChangeHook, CollectionBeforeValidateHook } from 'payload'
import { inspectAudioUpload } from '../domain/audioInspection'
import {
  assertAudioTransition,
  reviewStampForTransition,
  type AudioState,
} from '../domain/audioWorkflow'
import type { AvaUser } from '../domain/roles'

export const initializeAudioUpload: CollectionBeforeValidateHook = async ({
  data,
  operation,
  req,
}) => {
  const user = req.user as unknown as AvaUser
  if (!user?.role) throw new Error('authenticated_role_required')
  const next = data ?? {}

  if (operation === 'create') {
    next.creator = user.id
    next.status = 'quarantined'
    next.deliveryStatus = 'pending'
  }

  const file = req.file
  if (file?.data) {
    const inspection = inspectAudioUpload(file.data, file.mimetype)
    next.detectedMime = inspection.detectedMime
    next.sourceSha256 = inspection.sourceSha256
    next.inspectedAt = new Date().toISOString()
    next.status = 'quarantined'
  }

  return next
}

export const protectAudioWorkflow: CollectionBeforeChangeHook = async ({
  data,
  operation,
  originalDoc,
  req,
}) => {
  const user = req.user as unknown as AvaUser
  if (!user?.role) throw new Error('authenticated_role_required')
  if (operation === 'update') {
    const from = originalDoc.status as AudioState
    const to = (data.status ?? from) as AudioState
    if (from === 'approved' && to === from && user.role !== 'system_admin') {
      throw new Error('approved_audio_is_immutable')
    }
    assertAudioTransition(from, to, {
      actorId: user.id,
      actorRole: user.role,
      creatorId: originalDoc.creator,
      sourceInspected: Boolean(originalDoc.detectedMime && originalDoc.sourceSha256),
      technicalReviewerId: originalDoc.technicalReviewer,
    })
    Object.assign(data, reviewStampForTransition(from, to, user.id, new Date().toISOString()))
  }

  return data
}

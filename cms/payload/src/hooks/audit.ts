import type { CollectionAfterChangeHook } from 'payload'
import type { AvaUser } from '../domain/roles'

export const appendAuditEvent = (entityType: string): CollectionAfterChangeHook =>
  async ({ doc, operation, previousDoc, req }) => {
    const user = req.user as unknown as AvaUser
    await req.payload.create({
      collection: 'audit-events',
      data: {
        actor: user?.id,
        entityId: String(doc.id),
        entityStableId: String(doc.stableId ?? ''),
        entityType,
        eventType: operation,
        snapshot: {
          afterStatus: doc.status ?? doc.reviewStatus ?? doc.state ?? null,
          beforeStatus:
            previousDoc?.status ?? previousDoc?.reviewStatus ?? previousDoc?.state ?? null,
        },
      },
      overrideAccess: true,
      req,
    })
    return doc
  }

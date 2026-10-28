import type { CollectionBeforeChangeHook, CollectionBeforeValidateHook } from 'payload'
import { assertContentTransition, type ContentState } from '../domain/contentWorkflow'
import type { AvaUser } from '../domain/roles'

export const initializeContent: CollectionBeforeValidateHook = async ({ data, operation, req }) => {
  const user = req.user as unknown as AvaUser
  if (!user?.role) throw new Error('authenticated_role_required')
  const next = data ?? {}
  if (operation === 'create') {
    next.creator = user.id
    next.reviewStatus = 'draft'
  }
  return next
}

export const protectContentWorkflow: CollectionBeforeChangeHook = async ({
  data,
  operation,
  originalDoc,
  req,
}) => {
  const user = req.user as unknown as AvaUser
  if (!user?.role) throw new Error('authenticated_role_required')
  if (operation === 'update') {
    const from = originalDoc.reviewStatus as ContentState
    const to = (data.reviewStatus ?? from) as ContentState
    if (from === 'approved' && to === from && user.role !== 'system_admin') {
      throw new Error('approved_content_is_immutable')
    }
    assertContentTransition({
      actorId: user.id,
      actorRole: user.role,
      creatorId: originalDoc.creator,
      from,
      to,
    })
    if (from === 'educational_review' && to === 'approved') {
      data.educatorReviewer = user.id
      data.educatorReviewedAt = new Date().toISOString()
    }
  }
  return data
}

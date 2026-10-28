import type { AvaRole } from './roles'
import { sameActor } from './roles'

export const CONTENT_STATES = ['draft', 'educational_review', 'approved', 'rejected'] as const
export type ContentState = (typeof CONTENT_STATES)[number]

export function assertContentTransition(args: {
  actorId: number | string
  actorRole: AvaRole
  creatorId?: unknown
  from: ContentState
  to: ContentState
}): void {
  if (args.from === args.to) return
  const key = `${args.from}->${args.to}`
  const allowed: Record<string, readonly AvaRole[]> = {
    'draft->educational_review': ['content_author', 'system_admin'],
    'educational_review->approved': ['educator_reviewer', 'system_admin'],
    'educational_review->rejected': ['educator_reviewer', 'system_admin'],
    'rejected->draft': ['content_author', 'system_admin'],
    'approved->draft': ['system_admin'],
  }
  if (!allowed[key]?.includes(args.actorRole)) throw new Error(`content_transition_denied:${key}`)
  if (
    ['approved', 'rejected'].includes(args.to) &&
    sameActor(args.actorId, args.creatorId) &&
    args.actorRole !== 'system_admin'
  ) {
    throw new Error('content_self_review_denied')
  }
}

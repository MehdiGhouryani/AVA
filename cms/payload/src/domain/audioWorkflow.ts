import type { AvaRole } from './roles'
import { sameActor } from './roles'

export const AUDIO_STATES = [
  'quarantined',
  'technical_review',
  'educational_review',
  'approved',
  'rejected',
] as const

export type AudioState = (typeof AUDIO_STATES)[number]

export type AudioTransitionContext = {
  actorId: number | string
  actorRole: AvaRole
  creatorId?: unknown
  sourceInspected?: boolean
  technicalReviewerId?: unknown
}

const transitionRoles: Record<string, readonly AvaRole[]> = {
  'quarantined->technical_review': ['content_author', 'audio_reviewer', 'system_admin'],
  'technical_review->educational_review': ['audio_reviewer', 'system_admin'],
  'technical_review->rejected': ['audio_reviewer', 'system_admin'],
  'educational_review->approved': ['educator_reviewer', 'system_admin'],
  'educational_review->rejected': ['educator_reviewer', 'system_admin'],
  'rejected->quarantined': ['content_author', 'system_admin'],
  'approved->quarantined': ['system_admin'],
}

export function assertAudioTransition(
  from: AudioState,
  to: AudioState,
  context: AudioTransitionContext,
): void {
  if (from === to) return
  const roles = transitionRoles[`${from}->${to}`]
  if (!roles?.includes(context.actorRole)) {
    throw new Error(`audio_transition_denied:${from}->${to}`)
  }

  if (from === 'quarantined' && to === 'technical_review' && !context.sourceInspected) {
    throw new Error('audio_source_inspection_required')
  }

  if (
    ['educational_review', 'approved', 'rejected'].includes(to) &&
    sameActor(context.actorId, context.creatorId) &&
    context.actorRole !== 'system_admin'
  ) {
    throw new Error('audio_self_review_denied')
  }

  if (
    to === 'approved' &&
    sameActor(context.actorId, context.technicalReviewerId) &&
    context.actorRole !== 'system_admin'
  ) {
    throw new Error('audio_same_reviewer_denied')
  }
}

export type ReviewStamp = {
  technicalReviewer?: number | string
  technicalReviewedAt?: string
  educationalReviewer?: number | string
  educationalReviewedAt?: string
}

export function reviewStampForTransition(
  from: AudioState,
  to: AudioState,
  actorId: number | string,
  now: string,
): ReviewStamp {
  if (from === 'technical_review' && to === 'educational_review') {
    return { technicalReviewer: actorId, technicalReviewedAt: now }
  }
  if (from === 'educational_review' && to === 'approved') {
    return { educationalReviewer: actorId, educationalReviewedAt: now }
  }
  if (to === 'quarantined') {
    return {
      educationalReviewer: undefined,
      educationalReviewedAt: undefined,
      technicalReviewer: undefined,
      technicalReviewedAt: undefined,
    }
  }
  return {}
}

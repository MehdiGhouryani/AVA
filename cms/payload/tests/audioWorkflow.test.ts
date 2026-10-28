import { describe, expect, it } from 'vitest'
import { assertAudioTransition, reviewStampForTransition } from '../src/domain/audioWorkflow'

describe('audio review separation', () => {
  it('lets an author submit a quarantined upload', () => {
    expect(() =>
      assertAudioTransition('quarantined', 'technical_review', {
        actorId: 2,
        actorRole: 'content_author',
        creatorId: 2,
        sourceInspected: true,
      }),
    ).not.toThrow()
  })

  it('blocks submission before server-side source inspection', () => {
    expect(() =>
      assertAudioTransition('quarantined', 'technical_review', {
        actorId: 2,
        actorRole: 'content_author',
        creatorId: 2,
        sourceInspected: false,
      }),
    ).toThrow('audio_source_inspection_required')
  })

  it('requires the technical reviewer role', () => {
    expect(() =>
      assertAudioTransition('technical_review', 'educational_review', {
        actorId: 3,
        actorRole: 'educator_reviewer',
        creatorId: 2,
      }),
    ).toThrow('audio_transition_denied')
  })

  it('blocks creator self-review', () => {
    expect(() =>
      assertAudioTransition('technical_review', 'educational_review', {
        actorId: 2,
        actorRole: 'audio_reviewer',
        creatorId: 2,
      }),
    ).toThrow('audio_self_review_denied')
  })

  it('blocks one person from both review stages', () => {
    expect(() =>
      assertAudioTransition('educational_review', 'approved', {
        actorId: 3,
        actorRole: 'educator_reviewer',
        creatorId: 2,
        technicalReviewerId: 3,
      }),
    ).toThrow('audio_same_reviewer_denied')
  })

  it('allows independent educational approval', () => {
    expect(() =>
      assertAudioTransition('educational_review', 'approved', {
        actorId: 4,
        actorRole: 'educator_reviewer',
        creatorId: 2,
        technicalReviewerId: 3,
      }),
    ).not.toThrow()
  })

  it('stamps the responsible reviewer only', () => {
    expect(reviewStampForTransition('technical_review', 'educational_review', 3, '2026-01-01Z')).toEqual({
      technicalReviewer: 3,
      technicalReviewedAt: '2026-01-01Z',
    })
  })
})

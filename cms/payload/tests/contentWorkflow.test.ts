import { describe, expect, it } from 'vitest'
import { assertContentTransition } from '../src/domain/contentWorkflow'

describe('content review separation', () => {
  it('allows author submission and independent educator approval', () => {
    expect(() =>
      assertContentTransition({
        actorId: 2,
        actorRole: 'content_author',
        creatorId: 2,
        from: 'draft',
        to: 'educational_review',
      }),
    ).not.toThrow()
    expect(() =>
      assertContentTransition({
        actorId: 3,
        actorRole: 'educator_reviewer',
        creatorId: 2,
        from: 'educational_review',
        to: 'approved',
      }),
    ).not.toThrow()
  })

  it('blocks self-approval and invalid shortcuts', () => {
    expect(() =>
      assertContentTransition({
        actorId: 2,
        actorRole: 'educator_reviewer',
        creatorId: 2,
        from: 'educational_review',
        to: 'approved',
      }),
    ).toThrow('content_self_review_denied')
    expect(() =>
      assertContentTransition({
        actorId: 2,
        actorRole: 'content_author',
        creatorId: 2,
        from: 'draft',
        to: 'approved',
      }),
    ).toThrow('content_transition_denied')
  })
})

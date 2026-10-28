import { describe, expect, it } from 'vitest'
import { buildDeterministicExport } from '../src/domain/deterministicExport'
import { releaseBlockers } from '../src/domain/releasePolicy'

describe('release gates', () => {
  it('returns every blocker without short-circuiting', () => {
    expect(
      releaseBlockers({
        actorId: 1,
        actorRole: 'content_author',
        audio: [
          {
            deliveryObjectKey: 'delivery/a.mp3',
            deliverySha256: 'a'.repeat(64),
            deliveryStatus: 'ready',
            status: 'approved',
          },
          { deliveryStatus: 'pending', status: 'technical_review' },
        ],
        contentStates: ['draft'],
        creatorId: 1,
        scope: 'partial',
      }),
    ).toEqual([
      'publisher_role_required',
      'publisher_must_differ_from_creator',
      'cms_export_scope_partial',
      'content_not_educator_approved',
      'required_audio_not_approved',
      'required_audio_delivery_not_ready',
    ])
  })

  it('passes only a complete independently published candidate', () => {
    expect(
      releaseBlockers({
        actorId: 9,
        actorRole: 'publisher',
        audio: [
          {
            deliveryObjectKey: 'delivery/voice-a.mp3',
            deliverySha256: 'a'.repeat(64),
            deliveryStatus: 'ready',
            status: 'approved',
          },
        ],
        contentStates: ['approved'],
        creatorId: 1,
        scope: 'complete',
      }),
    ).toEqual([])
  })

  it('blocks approved masters that have no verified delivery artifact', () => {
    expect(
      releaseBlockers({
        actorId: 9,
        actorRole: 'publisher',
        audio: [{ deliveryStatus: 'pending', status: 'approved' }],
        contentStates: ['approved'],
        creatorId: 1,
        scope: 'complete',
      }),
    ).toEqual(['required_audio_delivery_not_ready'])
  })
})

describe('deterministic CMS export', () => {
  it('is byte-identical regardless of input record order', () => {
    const base = {
      candidateId: 'candidate-1',
      contentVersion: '0.6.1-dev.1',
      content: [
        { stableId: 'b', title: 'ب' },
        { stableId: 'a', title: 'آ' },
      ],
      audio: [
        { stableId: 'voice-b', sha256: '2' },
        { stableId: 'voice-a', sha256: '1' },
      ],
    }
    const first = buildDeterministicExport(base)
    const second = buildDeterministicExport({
      ...base,
      audio: [...base.audio].reverse(),
      content: [...base.content].reverse(),
    })
    expect(first.bytes.equals(second.bytes)).toBe(true)
    expect(first.sha256).toBe(second.sha256)
  })

  it('changes its digest when approved data changes', () => {
    const common = {
      candidateId: 'candidate-1',
      contentVersion: '1',
      audio: [] as { stableId: string }[],
    }
    const first = buildDeterministicExport({ ...common, content: [{ stableId: 'a', value: 1 }] })
    const second = buildDeterministicExport({ ...common, content: [{ stableId: 'a', value: 2 }] })
    expect(first.sha256).not.toBe(second.sha256)
  })
})

import type { AvaRole } from './roles'
import { sameActor } from './roles'

export type ReleaseGateInput = {
  actorId: number | string
  actorRole: AvaRole
  audio: readonly {
    deliveryObjectKey?: string | null
    deliverySha256?: string | null
    deliveryStatus?: string | null
    status?: string | null
  }[]
  contentStates: readonly string[]
  creatorId?: unknown
  scope: 'complete' | 'partial'
}

export function releaseBlockers(input: ReleaseGateInput): string[] {
  const blockers: string[] = []
  if (!['publisher', 'system_admin'].includes(input.actorRole)) blockers.push('publisher_role_required')
  if (sameActor(input.actorId, input.creatorId) && input.actorRole !== 'system_admin') {
    blockers.push('publisher_must_differ_from_creator')
  }
  if (input.scope !== 'complete') blockers.push('cms_export_scope_partial')
  if (input.contentStates.some((state) => state !== 'approved')) {
    blockers.push('content_not_educator_approved')
  }
  if (input.audio.some((audio) => audio.status !== 'approved')) {
    blockers.push('required_audio_not_approved')
  }
  if (
    input.audio.some(
      (audio) =>
        audio.deliveryStatus !== 'ready' ||
        !audio.deliveryObjectKey ||
        !/^[a-f0-9]{64}$/.test(audio.deliverySha256 ?? ''),
    )
  ) {
    blockers.push('required_audio_delivery_not_ready')
  }
  return blockers
}

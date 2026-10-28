import { createHash } from 'node:crypto'

export type ExportRecord = { stableId: string; [key: string]: unknown }

function canonical(value: unknown): unknown {
  if (Array.isArray(value)) return value.map(canonical)
  if (value && typeof value === 'object') {
    return Object.fromEntries(
      Object.entries(value as Record<string, unknown>)
        .sort(([left], [right]) => left.localeCompare(right))
        .map(([key, child]) => [key, canonical(child)]),
    )
  }
  return value
}

export function buildDeterministicExport(input: {
  candidateId: string
  contentVersion: string
  content: readonly ExportRecord[]
  audio: readonly ExportRecord[]
}) {
  const sort = (records: readonly ExportRecord[]) =>
    [...records].sort((left, right) => left.stableId.localeCompare(right.stableId)).map(canonical)
  const payload = canonical({
    audio: sort(input.audio),
    candidateId: input.candidateId,
    content: sort(input.content),
    contentVersion: input.contentVersion,
    contractVersion: 1,
    scope: 'complete',
  })
  const bytes = Buffer.from(JSON.stringify(payload))
  return {
    bytes,
    payload,
    sha256: createHash('sha256').update(bytes).digest('hex'),
  }
}

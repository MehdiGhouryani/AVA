import { createHash } from 'node:crypto'

export type DetectedAudio = 'audio/mpeg' | 'audio/mp4' | 'audio/wav'

const claimedAliases: Record<DetectedAudio, readonly string[]> = {
  'audio/mpeg': ['audio/mpeg', 'audio/mp3'],
  'audio/mp4': ['audio/mp4', 'audio/x-m4a', 'video/mp4'],
  'audio/wav': ['audio/wav', 'audio/wave', 'audio/x-wav'],
}

export function detectAudioContainer(bytes: Uint8Array): DetectedAudio | null {
  if (bytes.length >= 12) {
    const head = Buffer.from(bytes.subarray(0, 12))
    if (head.toString('ascii', 0, 4) === 'RIFF' && head.toString('ascii', 8, 12) === 'WAVE') {
      return 'audio/wav'
    }
    if (head.toString('ascii', 4, 8) === 'ftyp') return 'audio/mp4'
  }
  if (bytes.length >= 3 && Buffer.from(bytes.subarray(0, 3)).toString('ascii') === 'ID3') {
    return 'audio/mpeg'
  }
  if (bytes.length >= 2 && bytes[0] === 0xff && (bytes[1] & 0xe0) === 0xe0) {
    return 'audio/mpeg'
  }
  return null
}

export function inspectAudioUpload(bytes: Uint8Array, claimedMime: string) {
  if (bytes.length === 0) throw new Error('audio_file_empty')
  const detectedMime = detectAudioContainer(bytes)
  if (!detectedMime) throw new Error('audio_magic_unsupported')
  if (!claimedAliases[detectedMime].includes(claimedMime.toLowerCase())) {
    throw new Error(`audio_mime_mismatch:${claimedMime}->${detectedMime}`)
  }
  return {
    detectedMime,
    sourceSha256: createHash('sha256').update(bytes).digest('hex'),
  }
}

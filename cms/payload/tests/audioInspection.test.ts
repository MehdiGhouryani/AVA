import { describe, expect, it } from 'vitest'
import { detectAudioContainer, inspectAudioUpload } from '../src/domain/audioInspection'

const wav = Buffer.concat([Buffer.from('RIFF'), Buffer.alloc(4), Buffer.from('WAVEfmt ')])
const mp3 = Buffer.from('ID3\u0004\u0000\u0000')
const m4a = Buffer.concat([Buffer.alloc(4), Buffer.from('ftypM4A ')])

describe('audio upload inspection', () => {
  it('detects supported container signatures', () => {
    expect(detectAudioContainer(wav)).toBe('audio/wav')
    expect(detectAudioContainer(mp3)).toBe('audio/mpeg')
    expect(detectAudioContainer(m4a)).toBe('audio/mp4')
  })

  it('computes a stable source hash', () => {
    expect(inspectAudioUpload(wav, 'audio/x-wav')).toEqual({
      detectedMime: 'audio/wav',
      sourceSha256: '564867d8781dc7f4f43e66543e9db6631f464c90087e3cf87a04040f2e83217c',
    })
  })

  it('rejects MIME and magic mismatches', () => {
    expect(() => inspectAudioUpload(wav, 'audio/mpeg')).toThrow('audio_mime_mismatch')
  })

  it('rejects empty and unsupported files', () => {
    expect(() => inspectAudioUpload(Buffer.alloc(0), 'audio/wav')).toThrow('audio_file_empty')
    expect(() => inspectAudioUpload(Buffer.from('not audio'), 'audio/wav')).toThrow(
      'audio_magic_unsupported',
    )
  })
})

export const AVA_ROLES = [
  'content_author',
  'educator_reviewer',
  'audio_reviewer',
  'publisher',
  'auditor',
  'system_admin',
] as const

export type AvaRole = (typeof AVA_ROLES)[number]

export type AvaUser = {
  id: number
  role?: AvaRole | null
} | null | undefined

export const ROLE_LABELS: Record<AvaRole, string> = {
  content_author: 'نویسندهٔ محتوا',
  educator_reviewer: 'بازبین آموزشی',
  audio_reviewer: 'بازبین فنی صدا',
  publisher: 'ناشر',
  auditor: 'ممیز',
  system_admin: 'مدیر سامانه',
}

export function hasRole(user: AvaUser, allowed: readonly AvaRole[]): boolean {
  return Boolean(user?.role && allowed.includes(user.role))
}

export function sameActor(left: unknown, right: unknown): boolean {
  if (left == null || right == null) return false
  const leftId = typeof left === 'object' && 'id' in left ? (left as { id: unknown }).id : left
  const rightId = typeof right === 'object' && 'id' in right ? (right as { id: unknown }).id : right
  return String(leftId) === String(rightId)
}

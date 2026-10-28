import type { Access, FieldAccess } from 'payload'
import type { AvaRole, AvaUser } from './domain/roles'
import { hasRole } from './domain/roles'

const userFrom = (value: unknown): AvaUser => value as AvaUser

export const authenticated: Access = ({ req }) => Boolean(req.user)

export const roleAccess = (roles: readonly AvaRole[]): Access =>
  ({ req }) => hasRole(userFrom(req.user), roles)

export const roleFieldAccess = (roles: readonly AvaRole[]): FieldAccess =>
  ({ req }) => hasRole(userFrom(req.user), roles)

export const selfOrAdmin: Access = ({ req }) => {
  const user = userFrom(req.user)
  if (!user) return false
  if (hasRole(user, ['system_admin'])) return true
  return { id: { equals: user.id } }
}

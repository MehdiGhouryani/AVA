import type { CollectionConfig } from 'payload'
import { roleAccess, roleFieldAccess, selfOrAdmin } from '../access'
import { AVA_ROLES, ROLE_LABELS, type AvaUser } from '../domain/roles'

export const Users: CollectionConfig = {
  slug: 'users',
  admin: { useAsTitle: 'email' },
  auth: true,
  hooks: {
    beforeValidate: [
      async ({ data, operation, req }) => {
        if (operation !== 'create') return data
        const next = data ?? {}
        const result = await req.payload.count({ collection: 'users', overrideAccess: true, req })
        if (result.totalDocs === 0) next.role = 'system_admin'
        return next
      },
    ],
  },
  access: {
    admin: ({ req }) => (req.user as unknown as AvaUser)?.role === 'system_admin',
    create: async ({ req }) => {
      if (roleAccess(['system_admin'])({ req })) return true
      const result = await req.payload.count({ collection: 'users', overrideAccess: true, req })
      return result.totalDocs === 0
    },
    delete: roleAccess(['system_admin']),
    read: selfOrAdmin,
    update: selfOrAdmin,
  },
  fields: [
    {
      name: 'displayName',
      type: 'text',
      required: true,
    },
    {
      name: 'role',
      type: 'select',
      access: { update: roleFieldAccess(['system_admin']) },
      defaultValue: 'content_author',
      options: AVA_ROLES.map((value) => ({ label: ROLE_LABELS[value], value })),
      required: true,
    },
  ],
}

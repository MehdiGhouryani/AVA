import type { CollectionConfig } from 'payload'
import { authenticated, roleAccess } from '../access'
import { appendAuditEvent } from '../hooks/audit'
import { initializeRelease, protectReleaseWorkflow } from '../hooks/release'

const serverManaged = { create: () => false, update: () => false }

export const Releases: CollectionConfig = {
  slug: 'releases',
  admin: {
    defaultColumns: ['stableId', 'contentVersion', 'state', 'scope', 'updatedAt'],
    useAsTitle: 'stableId',
  },
  access: {
    create: roleAccess(['content_author', 'publisher', 'system_admin']),
    delete: roleAccess(['system_admin']),
    read: authenticated,
    update: roleAccess(['content_author', 'publisher', 'system_admin']),
  },
  hooks: {
    afterChange: [appendAuditEvent('release')],
    beforeChange: [protectReleaseWorkflow],
    beforeValidate: [initializeRelease],
  },
  fields: [
    { name: 'stableId', type: 'text', required: true, unique: true, index: true },
    { name: 'contentVersion', type: 'text', required: true },
    {
      name: 'state',
      type: 'select',
      defaultValue: 'draft',
      options: ['draft', 'ready', 'published', 'rejected'],
      required: true,
    },
    {
      name: 'scope',
      type: 'select',
      defaultValue: 'partial',
      options: ['partial', 'complete'],
      required: true,
    },
    { name: 'contentItems', type: 'relationship', relationTo: 'curriculum-items', hasMany: true, required: true },
    { name: 'requiredAudio', type: 'relationship', relationTo: 'audio-assets', hasMany: true, required: true },
    { name: 'creator', type: 'relationship', relationTo: 'users', required: true, access: serverManaged },
    { name: 'publisher', type: 'relationship', relationTo: 'users', admin: { readOnly: true }, access: serverManaged },
    { name: 'publishedAt', type: 'date', admin: { readOnly: true }, access: serverManaged },
    { name: 'exportPayload', type: 'json', admin: { readOnly: true }, access: serverManaged },
    { name: 'exportSha256', type: 'text', admin: { readOnly: true }, index: true, access: serverManaged },
  ],
}

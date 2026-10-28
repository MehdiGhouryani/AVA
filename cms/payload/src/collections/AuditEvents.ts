import type { CollectionConfig } from 'payload'
import { roleAccess } from '../access'

export const AuditEvents: CollectionConfig = {
  slug: 'audit-events',
  admin: {
    defaultColumns: ['createdAt', 'eventType', 'entityType', 'entityStableId', 'actor'],
    useAsTitle: 'entityStableId',
  },
  access: {
    create: () => false,
    delete: () => false,
    read: roleAccess(['auditor', 'system_admin']),
    update: () => false,
  },
  fields: [
    { name: 'eventType', type: 'text', required: true },
    { name: 'entityType', type: 'text', required: true, index: true },
    { name: 'entityId', type: 'text', required: true },
    { name: 'entityStableId', type: 'text', required: true, index: true },
    { name: 'actor', type: 'relationship', relationTo: 'users' },
    { name: 'snapshot', type: 'json', required: true },
  ],
}

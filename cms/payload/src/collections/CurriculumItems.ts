import type { CollectionConfig } from 'payload'
import { authenticated, roleAccess } from '../access'
import { appendAuditEvent } from '../hooks/audit'
import { initializeContent, protectContentWorkflow } from '../hooks/content'

const serverManaged = { create: () => false, update: () => false }

export const CurriculumItems: CollectionConfig = {
  slug: 'curriculum-items',
  admin: {
    defaultColumns: ['stableId', 'kind', 'titleFa', 'reviewStatus', 'updatedAt'],
    useAsTitle: 'titleFa',
  },
  access: {
    create: roleAccess(['content_author', 'system_admin']),
    delete: roleAccess(['system_admin']),
    read: authenticated,
    update: roleAccess(['content_author', 'educator_reviewer', 'system_admin']),
  },
  hooks: {
    afterChange: [appendAuditEvent('curriculum_item')],
    beforeChange: [protectContentWorkflow],
    beforeValidate: [initializeContent],
  },
  versions: { drafts: true, maxPerDoc: 30 },
  fields: [
    { name: 'stableId', type: 'text', required: true, unique: true, index: true },
    {
      name: 'kind',
      type: 'select',
      options: ['level', 'unit', 'lesson', 'task', 'story', 'story_page'],
      required: true,
    },
    { name: 'titleFa', type: 'text', required: true },
    { name: 'parentStableId', type: 'text', index: true },
    { name: 'order', type: 'number', min: 0, required: true },
    { name: 'body', type: 'json', required: true },
    {
      name: 'reviewStatus',
      type: 'select',
      defaultValue: 'draft',
      options: ['draft', 'educational_review', 'approved', 'rejected'],
      required: true,
    },
    { name: 'creator', type: 'relationship', relationTo: 'users', required: true, access: serverManaged },
    { name: 'educatorReviewer', type: 'relationship', relationTo: 'users', admin: { readOnly: true }, access: serverManaged },
    { name: 'educatorReviewedAt', type: 'date', admin: { readOnly: true }, access: serverManaged },
    { name: 'reviewNotes', type: 'textarea' },
  ],
}

import type { CollectionConfig } from 'payload'
import { authenticated, roleAccess } from '../access'
import { appendAuditEvent } from '../hooks/audit'
import { initializeAudioUpload, protectAudioWorkflow } from '../hooks/audio'

const serverManaged = { create: () => false, update: () => false }

export const AudioAssets: CollectionConfig = {
  slug: 'audio-assets',
  admin: {
    defaultColumns: ['stableId', 'promptText', 'status', 'detectedMime', 'updatedAt'],
    useAsTitle: 'stableId',
  },
  access: {
    create: roleAccess(['content_author', 'audio_reviewer', 'system_admin']),
    delete: roleAccess(['system_admin']),
    read: authenticated,
    update: roleAccess([
      'content_author',
      'audio_reviewer',
      'educator_reviewer',
      'system_admin',
    ]),
  },
  hooks: {
    afterChange: [appendAuditEvent('audio_asset')],
    beforeChange: [protectAudioWorkflow],
    beforeValidate: [initializeAudioUpload],
  },
  upload: {
    mimeTypes: ['audio/mpeg', 'audio/mp4', 'audio/wav', 'audio/x-m4a', 'audio/x-wav'],
    staticDir: 'audio-assets',
  },
  fields: [
    { name: 'stableId', type: 'text', required: true, unique: true, index: true },
    { name: 'promptText', type: 'text', required: true },
    { name: 'speakerCode', type: 'text', required: true },
    {
      name: 'status',
      type: 'select',
      defaultValue: 'quarantined',
      options: [
        { label: 'قرنطینه', value: 'quarantined' },
        { label: 'بازبینی فنی', value: 'technical_review' },
        { label: 'بازبینی آموزشی', value: 'educational_review' },
        { label: 'تأییدشده', value: 'approved' },
        { label: 'ردشده', value: 'rejected' },
      ],
      required: true,
    },
    { name: 'creator', type: 'relationship', relationTo: 'users', required: true, access: serverManaged },
    { name: 'detectedMime', type: 'text', admin: { readOnly: true }, access: serverManaged },
    { name: 'sourceSha256', type: 'text', admin: { readOnly: true }, index: true, access: serverManaged },
    { name: 'inspectedAt', type: 'date', admin: { readOnly: true }, access: serverManaged },
    { name: 'technicalReviewer', type: 'relationship', relationTo: 'users', admin: { readOnly: true }, access: serverManaged },
    { name: 'technicalReviewedAt', type: 'date', admin: { readOnly: true }, access: serverManaged },
    { name: 'educationalReviewer', type: 'relationship', relationTo: 'users', admin: { readOnly: true }, access: serverManaged },
    { name: 'educationalReviewedAt', type: 'date', admin: { readOnly: true }, access: serverManaged },
    { name: 'licenseConsentRef', type: 'text', required: true },
    {
      name: 'deliveryStatus',
      type: 'select',
      admin: { readOnly: true },
      access: serverManaged,
      defaultValue: 'pending',
      options: ['pending', 'processing', 'ready', 'failed'],
      required: true,
    },
    { name: 'deliveryObjectKey', type: 'text', admin: { readOnly: true }, access: serverManaged },
    { name: 'deliverySha256', type: 'text', admin: { readOnly: true }, access: serverManaged },
    {
      name: 'deliveryMime',
      type: 'select',
      options: ['audio/mpeg', 'audio/mp4'],
      admin: { readOnly: true },
      access: serverManaged,
    },
    { name: 'deliveryBytes', type: 'number', min: 1, admin: { readOnly: true }, access: serverManaged },
    { name: 'deliveryErrorCode', type: 'text', admin: { readOnly: true }, access: serverManaged },
    { name: 'reviewNotes', type: 'textarea' },
  ],
}

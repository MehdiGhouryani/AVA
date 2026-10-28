import { postgresAdapter } from '@payloadcms/db-postgres'
import { lexicalEditor } from '@payloadcms/richtext-lexical'
import { s3Storage } from '@payloadcms/storage-s3'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { buildConfig } from 'payload'
import sharp from 'sharp'

import { AudioAssets } from './collections/AudioAssets'
import { AuditEvents } from './collections/AuditEvents'
import { CurriculumItems } from './collections/CurriculumItems'
import { Releases } from './collections/Releases'
import { Users } from './collections/Users'

const filename = fileURLToPath(import.meta.url)
const dirname = path.dirname(filename)
const s3Enabled = Boolean(process.env.S3_BUCKET)

export default buildConfig({
  admin: {
    meta: { titleSuffix: '— AVA' },
    user: Users.slug,
  },
  collections: [Users, CurriculumItems, AudioAssets, Releases, AuditEvents],
  db: postgresAdapter({
    pool: { connectionString: process.env.DATABASE_URL ?? '' },
  }),
  editor: lexicalEditor(),
  plugins: [
    s3Storage({
      bucket: process.env.S3_BUCKET ?? 'disabled',
      collections: {
        'audio-assets': {
          prefix: 'quarantine',
          signedDownloads: true,
        },
      },
      config: {
        credentials: {
          accessKeyId: process.env.S3_ACCESS_KEY_ID ?? '',
          secretAccessKey: process.env.S3_SECRET_ACCESS_KEY ?? '',
        },
        endpoint: process.env.S3_ENDPOINT,
        forcePathStyle: process.env.S3_FORCE_PATH_STYLE === 'true',
        region: process.env.S3_REGION ?? 'us-east-1',
      },
      enabled: s3Enabled,
    }),
  ],
  secret: process.env.PAYLOAD_SECRET ?? '',
  sharp,
  typescript: {
    outputFile: path.resolve(dirname, 'payload-types.ts'),
  },
  upload: {
    limits: { fileSize: 25 * 1024 * 1024 },
  },
})

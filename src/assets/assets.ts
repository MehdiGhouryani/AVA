import manifest from '../../design/assets.manifest.json';

export interface AssetMeta {
  id: string;
  src: string;
  kind: string;
  width: number;
  height: number;
  hasAlpha: boolean;
  sizeKB: number;
  usedIn: string[];
  notes?: string;
}

export type AssetId =
  | 'bg.splash'
  | 'bg.onboarding1'
  | 'bg.garden'
  | 'bg.login'
  | 'brand.logo'
  | 'char.ava.head_happy'
  | 'char.ava.reading'
  | 'char.ava.wave_book'
  | 'char.friend.bunny'
  | 'char.friend.squirrel'
  | 'char.friend.fox'
  | 'decor.login.top_left'
  | 'decor.login.bottom_left'
  | 'decor.login.bottom_right'
  | 'decor.tree'
  | 'decor.magic_book';

const assetMap = new Map<string, AssetMeta>();

(manifest.assets as AssetMeta[]).forEach((item) => {
  assetMap.set(item.id, item);
});

export function asset(id: AssetId | string): AssetMeta {
  const found = assetMap.get(id);
  if (!found) {
    console.warn(`[Assets] Asset not found in manifest: ${id}`);
    return {
      id,
      src: '',
      kind: 'unknown',
      width: 100,
      height: 100,
      hasAlpha: true,
      sizeKB: 0,
      usedIn: []
    };
  }
  return found;
}

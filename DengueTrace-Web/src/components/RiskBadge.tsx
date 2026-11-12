import clsx from 'clsx';
import type { RiskLevel } from '../types/api';
import { RISK_META } from '../utils/constants';

/** Texto legible sobre el color de cada nivel (los tonos claros necesitan texto oscuro). */
const RISK_TEXT: Record<RiskLevel, string> = {
  LOW: '#7c2d12',
  MEDIUM: '#78350f',
  HIGH: '#ffffff',
  CRITICAL: '#ffffff',
};

export function RiskBadge({ level, className }: { level: RiskLevel; className?: string }) {
  const meta = RISK_META[level];
  return (
    <span
      className={clsx(
        'inline-flex items-center gap-1.5 rounded-full px-2.5 py-1 text-xs font-semibold',
        className,
      )}
      style={{ backgroundColor: meta.color, color: RISK_TEXT[level] }}
    >
      <span className="h-1.5 w-1.5 rounded-full bg-current" aria-hidden />
      {meta.label}
    </span>
  );
}
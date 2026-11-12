import clsx from 'clsx';
import { AlertTriangle, BellOff, ChevronRight, MapPin, RefreshCw, Settings } from 'lucide-react';
import { useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { RiskBadge } from '../../components/RiskBadge';
import { useAuth } from '../../hooks/useAuth';
import { useFetch } from '../../hooks/useFetch';
import { alertService } from '../../services/alertService';
import type { Alert, RiskLevel } from '../../types/api';
import { RISK_META } from '../../utils/constants';

const RISK_ORDER: RiskLevel[] = ['CRITICAL', 'HIGH', 'MEDIUM', 'LOW'];

const dateFormatter = new Intl.DateTimeFormat('es-PE', { dateStyle: 'medium', timeStyle: 'short' });

function formatDate(value: string) {
  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? 'Fecha desconocida' : dateFormatter.format(date);
}

export default function AlertsPage() {
  const { hasRole } = useAuth();
  const { data, loading, error, reload } = useFetch<Alert[]>(signal => alertService.getActive(signal), []);

  const alerts = useMemo(() => data ?? [], [data]);
  const [riskFilter, setRiskFilter] = useState<RiskLevel | 'ALL'>('ALL');
  const [districtFilter, setDistrictFilter] = useState<number | 'ALL'>('ALL');

  const districts = useMemo(() => {
    const byId = new Map<number, string>();
    for (const alert of alerts) byId.set(alert.districtId, alert.districtName);
    return [...byId.entries()]
      .map(([id, name]) => ({ id, name }))
      .sort((a, b) => a.name.localeCompare(b.name, 'es'));
  }, [alerts]);

  const countsByRisk = useMemo(() => {
    const counts: Record<RiskLevel, number> = { LOW: 0, MEDIUM: 0, HIGH: 0, CRITICAL: 0 };
    for (const alert of alerts) counts[alert.riskLevel] += 1;
    return counts;
  }, [alerts]);

  const filteredAlerts = useMemo(
    () =>
      alerts
        .filter(alert => riskFilter === 'ALL' || alert.riskLevel === riskFilter)
        .filter(alert => districtFilter === 'ALL' || alert.districtId === districtFilter)
        .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()),
    [alerts, riskFilter, districtFilter],
  );

  const filtersActive = riskFilter !== 'ALL' || districtFilter !== 'ALL';

  const clearFilters = () => {
    setRiskFilter('ALL');
    setDistrictFilter('ALL');
  };

  return (
    <section>
      <div className="flex flex-wrap items-start justify-between gap-4">
        <div>
          <h1 className="text-2xl font-semibold">Alertas de riesgo</h1>
          <p className="mt-1 text-sm text-slate-600 dark:text-slate-400">
            Avisos activos emitidos por vigilancia epidemiológica para cada distrito.
          </p>
        </div>

        <div className="flex items-center gap-2">
          {hasRole('ADMIN') && (
            <Link
              to="/admin/alerts"
              className="inline-flex items-center gap-2 rounded-lg border border-slate-300 px-3 py-2 text-sm font-medium text-slate-700 hover:bg-slate-100 dark:border-slate-700 dark:text-slate-200 dark:hover:bg-slate-800"
            >
              <Settings className="h-4 w-4" aria-hidden />
              Gestionar
            </Link>
          )}
          <button
            type="button"
            onClick={reload}
            disabled={loading}
            className="inline-flex items-center gap-2 rounded-lg bg-sky-900 px-3 py-2 text-sm font-medium text-white hover:bg-sky-800 disabled:cursor-not-allowed disabled:opacity-60"
          >
            <RefreshCw className={clsx('h-4 w-4', loading && 'animate-spin')} aria-hidden />
            Actualizar
          </button>
        </div>
      </div>

      <dl className="mt-6 grid grid-cols-2 gap-3 sm:grid-cols-4">
        {RISK_ORDER.map(level => (
          <div
            key={level}
            className="rounded-xl border border-slate-200 bg-white p-3 dark:border-slate-800 dark:bg-slate-900"
          >
            <dt className="flex items-center gap-2 text-xs font-medium text-slate-500 dark:text-slate-400">
              <span
                className="h-2.5 w-2.5 rounded-full"
                style={{ backgroundColor: RISK_META[level].color }}
                aria-hidden
              />
              {RISK_META[level].label}
            </dt>
            <dd className="mt-1 text-2xl font-semibold tabular-nums">{countsByRisk[level]}</dd>
          </div>
        ))}
      </dl>

      <div className="mt-6 flex flex-wrap items-center gap-3">
        <div className="flex flex-wrap gap-2" role="group" aria-label="Filtrar por nivel de riesgo">
          <button
            type="button"
            onClick={() => setRiskFilter('ALL')}
            aria-pressed={riskFilter === 'ALL'}
            className={clsx(
              'rounded-full border px-3 py-1.5 text-xs font-semibold transition',
              riskFilter === 'ALL'
                ? 'border-sky-900 bg-sky-900 text-white'
                : 'border-slate-300 bg-white text-slate-700 hover:bg-slate-100 dark:border-slate-700 dark:bg-slate-900 dark:text-slate-200 dark:hover:bg-slate-800',
            )}
          >
            Todas
          </button>
          {RISK_ORDER.map(level => (
            <button
              key={level}
              type="button"
              onClick={() => setRiskFilter(level)}
              aria-pressed={riskFilter === level}
              className={clsx(
                'rounded-full border px-3 py-1.5 text-xs font-semibold transition',
                riskFilter === level
                  ? 'border-sky-900 bg-sky-900 text-white'
                  : 'border-slate-300 bg-white text-slate-700 hover:bg-slate-100 dark:border-slate-700 dark:bg-slate-900 dark:text-slate-200 dark:hover:bg-slate-800',
              )}
            >
              {RISK_META[level].label}
            </button>
          ))}
        </div>

        <label className="flex items-center gap-2 text-sm text-slate-600 dark:text-slate-400">
          <span className="sr-only">Filtrar por distrito</span>
          <select
            value={districtFilter}
            onChange={event =>
              setDistrictFilter(event.target.value === 'ALL' ? 'ALL' : Number(event.target.value))
            }
            className="rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm text-slate-700 dark:border-slate-700 dark:bg-slate-900 dark:text-slate-200"
          >
            <option value="ALL">Todos los distritos</option>
            {districts.map(district => (
              <option key={district.id} value={district.id}>
                {district.name}
              </option>
            ))}
          </select>
        </label>

        {filtersActive && (
          <button
            type="button"
            onClick={clearFilters}
            className="text-sm font-medium text-sky-800 underline hover:no-underline dark:text-sky-300"
          >
            Limpiar filtros
          </button>
        )}
      </div>

      <p className="mt-4 text-sm text-slate-600 dark:text-slate-400" aria-live="polite">
        {loading && !data
          ? 'Cargando alertas…'
          : `${filteredAlerts.length} ${filteredAlerts.length === 1 ? 'alerta' : 'alertas'}`}
      </p>

      {error && !data && (
        <div
          role="alert"
          className="mt-4 rounded-xl border border-red-200 bg-red-50 p-6 text-center dark:border-red-900 dark:bg-red-950/40"
        >
          <AlertTriangle className="mx-auto h-10 w-10 text-red-600 dark:text-red-400" aria-hidden />
          <h2 className="mt-3 text-lg font-semibold">No pudimos cargar las alertas</h2>
          <p className="mt-1 text-sm text-slate-600 dark:text-slate-400">{error.message}</p>
          {error.fieldErrors.length > 0 && (
            <ul className="mt-2 list-inside list-disc text-left text-sm text-slate-600 dark:text-slate-400">
              {error.fieldErrors.map(detail => (
                <li key={detail}>{detail}</li>
              ))}
            </ul>
          )}
          <button
            type="button"
            onClick={reload}
            className="mt-4 inline-flex items-center gap-2 rounded-lg bg-sky-900 px-4 py-2 text-sm font-medium text-white hover:bg-sky-800"
          >
            <RefreshCw className="h-4 w-4" aria-hidden />
            Reintentar
          </button>
        </div>
      )}

      {error && data && (
        <div
          role="alert"
          className="mt-4 flex flex-wrap items-center justify-between gap-2 rounded-lg border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-900 dark:border-amber-900 dark:bg-amber-950/40 dark:text-amber-200"
        >
          <span>No pudimos actualizar las alertas: {error.message}</span>
          <button type="button" onClick={reload} className="font-medium underline hover:no-underline">
            Reintentar
          </button>
        </div>
      )}

      {loading && !data && (
        <ul className="mt-4 space-y-3" aria-label="Cargando alertas">
          {[0, 1, 2].map(index => (
            <li
              key={index}
              className="h-28 animate-pulse rounded-xl bg-slate-200 dark:bg-slate-800"
            />
          ))}
        </ul>
      )}

      {!loading && !error && filteredAlerts.length === 0 && (
        <div className="mt-4 rounded-xl border border-dashed border-slate-300 bg-white p-10 text-center dark:border-slate-700 dark:bg-slate-900">
          <BellOff className="mx-auto h-10 w-10 text-slate-400" aria-hidden />
          <h2 className="mt-3 text-lg font-semibold">
            {filtersActive ? 'No hay alertas con esos filtros' : 'No hay alertas activas'}
          </h2>
          <p className="mt-1 text-sm text-slate-600 dark:text-slate-400">
            {filtersActive
              ? 'Prueba con otro nivel de riesgo u otro distrito.'
              : 'Cuando vigilancia epidemiológica emita una alerta, aparecerá aquí.'}
          </p>
          {filtersActive && (
            <button
              type="button"
              onClick={clearFilters}
              className="mt-4 text-sm font-medium text-sky-800 underline hover:no-underline dark:text-sky-300"
            >
              Limpiar filtros
            </button>
          )}
        </div>
      )}

      {filteredAlerts.length > 0 && (
        <ul className="mt-4 space-y-3">
          {filteredAlerts.map(alert => (
            <li key={alert.id}>
              <Link
                to={`/districts/${alert.districtId}`}
                className="group block rounded-xl border border-slate-200 bg-white p-4 transition hover:border-sky-300 hover:shadow-sm dark:border-slate-800 dark:bg-slate-900 dark:hover:border-sky-700"
              >
                <div className="flex flex-wrap items-center justify-between gap-3">
                  <div className="flex flex-wrap items-center gap-2">
                    <RiskBadge level={alert.riskLevel} />
                    <span className="inline-flex items-center gap-1 text-sm font-medium text-slate-700 dark:text-slate-200">
                      <MapPin className="h-4 w-4" aria-hidden />
                      {alert.districtName}
                    </span>
                  </div>
                  <time
                    dateTime={alert.createdAt}
                    className="text-xs text-slate-500 dark:text-slate-400"
                  >
                    {formatDate(alert.createdAt)}
                  </time>
                </div>

                <p className="mt-3 text-sm leading-relaxed text-slate-700 dark:text-slate-300">
                  {alert.message}
                </p>

                <div className="mt-3 flex flex-wrap items-center justify-between gap-2 text-xs text-slate-500 dark:text-slate-400">
                  <span>
                    {alert.thresholdExceeded != null
                      ? `Umbral: ${alert.thresholdExceeded}`
                      : 'Sin umbral asociado'}
                  </span>
                  <span className="inline-flex items-center gap-1 font-medium text-sky-800 dark:text-sky-300">
                    Ver distrito
                    <ChevronRight
                      className="h-4 w-4 transition group-hover:translate-x-0.5"
                      aria-hidden
                    />
                  </span>
                </div>
              </Link>
            </li>
          ))}
        </ul>
      )}
    </section>
  );
}

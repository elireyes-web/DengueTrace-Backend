import { AlertOctagon, Home, RotateCcw } from 'lucide-react';
import { isRouteErrorResponse, Link, useRouteError } from 'react-router-dom';

interface ErrorCopy {
  code: string;
  title: string;
  description: string;
}

function getCopy(error: unknown): ErrorCopy {
  if (isRouteErrorResponse(error)) {
    if (error.status === 404) {
      return {
        code: '404',
        title: 'No encontramos esta página',
        description: 'El recurso que buscas no existe o fue movido de lugar.',
      };
    }
    if (error.status === 401 || error.status === 403) {
      return {
        code: String(error.status),
        title: 'No tienes acceso a esta página',
        description: 'Inicia sesión con una cuenta autorizada o vuelve al inicio.',
      };
    }
    return {
      code: String(error.status),
      title: 'No pudimos abrir esta página',
      description: error.statusText || 'Ocurrió un error al cargar la vista.',
    };
  }

  return {
    code: 'Error',
    title: 'Algo salió mal',
    description: 'Ocurrió un error inesperado mientras cargábamos la página.',
  };
}

/**
 * Pantalla de error de nivel de ruta (`errorElement`). Cubre errores de
 * navegación/loader; los errores de renderizado dentro del layout los atrapa
 * `<ErrorBoundary>` para no perder la navegación.
 */
export default function RouteErrorPage() {
  const error = useRouteError();
  const { code, title, description } = getCopy(error);

  const technicalDetail =
    import.meta.env.DEV && !isRouteErrorResponse(error) && error instanceof Error
      ? error.message : null;

  return (
    <main className="flex min-h-screen items-center justify-center bg-slate-50 px-4 py-16 text-slate-900 dark:bg-slate-950 dark:text-slate-100">
      <div className="w-full max-w-lg text-center">
        <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-2xl bg-red-100 text-red-700 dark:bg-red-950 dark:text-red-300">
          <AlertOctagon className="h-8 w-8" aria-hidden />
        </div>

        <p className="mt-6 text-sm font-semibold uppercase tracking-widest text-slate-500 dark:text-slate-400">
          {code}
        </p>
        <h1 className="mt-2 text-3xl font-bold">{title}</h1>
        <p className="mt-3 text-slate-600 dark:text-slate-400">{description}</p>

        {technicalDetail && (
          <details className="mt-6 text-left">
            <summary className="cursor-pointer text-sm font-medium text-slate-600 dark:text-slate-400">
              Detalle técnico
            </summary>
            <pre className="mt-2 overflow-auto rounded-lg bg-slate-900 p-3 text-xs text-slate-100">
              {technicalDetail}
            </pre>
          </details>
        )}

        <div className="mt-8 flex flex-wrap justify-center gap-3">
          <Link
            to="/"
            className="inline-flex items-center gap-2 rounded-lg bg-sky-900 px-4 py-2 text-sm font-medium text-white hover:bg-sky-800"
          >
            <Home className="h-4 w-4" aria-hidden />
            Ir al inicio
          </Link>
          <button
            type="button"
            onClick={() => window.location.reload()}
            className="inline-flex items-center gap-2 rounded-lg border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700 hover:bg-slate-100 dark:border-slate-700 dark:text-slate-200 dark:hover:bg-slate-800"
          >
            <RotateCcw className="h-4 w-4" aria-hidden />
            Reintentar
          </button>
        </div>
      </div>
    </main>
  );
}

import { ArrowLeft, Home, SearchX } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

/**
 * 404 de la aplicación. Se renderiza fuera de AppLayout (ruta "*"), por eso
 * incluye su propio contenedor de pantalla completa.
 */
export default function NotFoundPage() {
  const { status } = useAuth();
  const navigate = useNavigate();
  const homePath = status === 'authenticated' ? '/dashboard' : '/login';

  return (
    <main className="flex min-h-screen items-center justify-center bg-slate-50 px-4 py-16 text-slate-900 dark:bg-slate-950 dark:text-slate-100">
      <div className="w-full max-w-lg text-center">
        <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-2xl bg-sky-100 text-sky-900 dark:bg-sky-950 dark:text-sky-300">
          <SearchX className="h-8 w-8" aria-hidden />
        </div>

        <p className="mt-6 text-sm font-semibold uppercase tracking-widest text-slate-500 dark:text-slate-400">
          Error 404
        </p>
        <h1 className="mt-2 text-3xl font-bold">No encontramos esta página</h1>
        <p className="mt-3 text-slate-600 dark:text-slate-400">
          Puede que el enlace esté mal escrito o que la página ya no exista.
        </p>

        <div className="mt-8 flex flex-wrap justify-center gap-3">
          <Link
            to={homePath}
            className="inline-flex items-center gap-2 rounded-lg bg-sky-900 px-4 py-2 text-sm font-medium text-white hover:bg-sky-800"
          >
            <Home className="h-4 w-4" aria-hidden />
            Ir al inicio
          </Link>
          <button
            type="button"
            onClick={() => navigate(-1)}
            className="inline-flex items-center gap-2 rounded-lg border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700 hover:bg-slate-100 dark:border-slate-700 dark:text-slate-200 dark:hover:bg-slate-800"
          >
            <ArrowLeft className="h-4 w-4" aria-hidden />
            Volver
          </button>
        </div>

        {status === 'authenticated' && (
          <p className="mt-8 text-sm text-slate-500 dark:text-slate-400">
            También puedes revisar las{' '}
            <Link to="/alerts" className="font-medium text-sky-800 underline dark:text-sky-300">
              alertas activas
            </Link>{' '}
            o tus{' '}
            <Link to="/reports/me" className="font-medium text-sky-800 underline dark:text-sky-300">
              reportes
            </Link>
            .
          </p>
        )}
      </div>
    </main>
  );
}

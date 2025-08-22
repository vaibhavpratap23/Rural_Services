import { Link, Outlet, useNavigate } from 'react-router-dom'
import { useEffect, useState } from 'react'
import { useAuth } from '../lib/auth'
import { useWebSocket } from '../hooks/useWebSocket'
import NotificationBell from '../components/NotificationBell'
import LanguageSelector from '../components/LanguageSelector'

export default function RootLayout() {
  const navigate = useNavigate()
  const { user, token, logout } = useAuth()
  const { connected } = useWebSocket(user?.id?.toString())

  return (
    <div className="min-h-screen flex flex-col">
      <header className="bg-white border-b">
        <div className="max-w-6xl mx-auto px-4 py-3 flex items-center justify-between">
          <Link to="/" className="font-semibold text-xl">GigFinder</Link>
                     <nav className="flex items-center gap-4">
             {!token && (
               <>
                 <Link to="/client" className="text-sm">For Clients</Link>
                 <Link to="/worker" className="text-sm">For Workers</Link>
               </>
             )}
             {token && user?.role === 'CLIENT' && (
               <Link to="/client" className="text-sm bg-blue-100 text-blue-700 px-3 py-1 rounded">My Dashboard</Link>
             )}
             {token && user?.role === 'WORKER' && (
               <Link to="/worker" className="text-sm bg-green-100 text-green-700 px-3 py-1 rounded">My Dashboard</Link>
             )}
             {user?.role === 'ADMIN' && (
               <Link to="/admin" className="text-sm bg-purple-100 text-purple-700 px-3 py-1 rounded">Admin Panel</Link>
             )}
                         {token ? (
               <div className="flex items-center gap-3">
                 <span className="text-sm">Hi, {user?.name?.split(' ')[0] || 'User'}</span>
                 <NotificationBell />
                 <LanguageSelector />
                 <Link to="/profile" className="text-sm">Profile</Link>
                 <button className="px-3 py-1.5 rounded bg-gray-100 text-sm" onClick={logout}>Logout</button>
               </div>
             ) : (
              <>
                <Link to="/login" className="text-sm">Login</Link>
                <Link to="/register" className="text-sm">Register</Link>
              </>
            )}
          </nav>
        </div>
      </header>
      <main className="flex-1">
        <Outlet />
      </main>
      <footer className="border-t bg-white">
        <div className="max-w-6xl mx-auto px-4 py-6 text-sm text-gray-500">© {new Date().getFullYear()} GigFinder</div>
      </footer>
    </div>
  )
}



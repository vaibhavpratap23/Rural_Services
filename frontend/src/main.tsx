import React from 'react'
import ReactDOM from 'react-dom/client'
import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import './index.css'
import RootLayout from './pages/RootLayout'
import { AuthProvider } from './lib/auth'
import { ToastProvider } from './hooks/useToast'
import Landing from './pages/Landing'
import Login from './pages/auth/Login'
import Register from './pages/auth/Register'
import ClientDashboard from './pages/client/ClientDashboard'
import WorkerDashboard from './pages/worker/WorkerDashboard'
import Profile from './pages/Profile'
import AdminDashboard from './components/AdminDashboard'

const router = createBrowserRouter([
  {
    path: '/',
    element: <RootLayout />,
    children: [
      { index: true, element: <Landing /> },
      { path: 'login', element: <Login /> },
      { path: 'register', element: <Register /> },
      { path: 'client', element: <ClientDashboard /> },
      { path: 'worker', element: <WorkerDashboard /> },
      { path: 'profile', element: <Profile /> },
      { path: 'admin', element: <AdminDashboard /> },
    ]
  }
])

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <ToastProvider>
      <AuthProvider>
        <RouterProvider router={router} />
      </AuthProvider>
    </ToastProvider>
  </React.StrictMode>
)



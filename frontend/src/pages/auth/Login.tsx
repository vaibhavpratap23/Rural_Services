import { FormEvent, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../lib/auth'

export default function Login() {
  const navigate = useNavigate()
  const { login } = useAuth()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [show, setShow] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setError(null)
    try {
      await login(email, password)
      const role = (JSON.parse(localStorage.getItem('user') || '{"role":"CLIENT"}')).role
      navigate(role === 'WORKER' ? '/worker' : '/client')
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Login failed')
    }
  }

  return (
    <div className="max-w-md mx-auto px-4 py-12">
      <h2 className="text-2xl font-semibold mb-6">Login</h2>
      <form onSubmit={onSubmit} className="space-y-4">
        <input className="w-full border rounded px-3 py-2" placeholder="Email" type="email" value={email} onChange={e => setEmail(e.target.value)} />
        <div className="relative">
          <input className="w-full border rounded px-3 py-2 pr-20" placeholder="Password" type={show ? 'text' : 'password'} value={password} onChange={e => setPassword(e.target.value)} />
          <button type="button" className="absolute right-2 top-1/2 -translate-y-1/2 text-sm" onClick={() => setShow(s => !s)}>{show ? 'Hide' : 'Show'}</button>
        </div>
        {error && <div className="text-sm text-red-600">{error}</div>}
        <button className="w-full bg-blue-600 text-white rounded px-3 py-2">Login</button>
      </form>
    </div>
  )
}



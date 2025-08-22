import { useEffect, useState } from 'react'
import axios from 'axios'
import { useAuth } from '../lib/auth'

type Job = { id: number; title: string; status: string; budget?: number; createdAt?: string }
type Payment = { id: number; amount: number; status: string; createdAt?: string }

export default function Profile() {
  const { user } = useAuth()
  const [jobs, setJobs] = useState<Job[]>([])
  const [payments, setPayments] = useState<Payment[]>([])

  useEffect(() => {
    axios.get('/api/jobs/me').then(r => setJobs(r.data)).catch(()=>{})
    axios.get('/api/payments/client').then(r => setPayments(r.data)).catch(()=>{})
  }, [])

  return (
    <div className="max-w-6xl mx-auto px-4 py-8 space-y-8">
      <div className="bg-white border rounded p-6">
        <div className="text-xl font-semibold">Profile</div>
        <div className="mt-2 text-sm text-gray-600">{user?.name} · {user?.email} · {user?.role}</div>
      </div>

      <div className="grid md:grid-cols-2 gap-6">
        <div className="bg-white border rounded p-6">
          <div className="font-semibold mb-3">My Jobs</div>
          <div className="space-y-3 max-h-96 overflow-auto">
            {jobs.map(j => (
              <div key={j.id} className="border rounded p-3">
                <div className="font-medium">{j.title}</div>
                <div className="text-xs text-gray-600">{j.status} · ₹{j.budget ?? '-'} · {j.createdAt?.slice(0,10)}</div>
              </div>
            ))}
            {jobs.length === 0 && <div className="text-sm text-gray-600">No jobs yet.</div>}
          </div>
        </div>
        <div className="bg-white border rounded p-6">
          <div className="font-semibold mb-3">Payment History</div>
          <div className="space-y-3 max-h-96 overflow-auto">
            {payments.map(p => (
              <div key={p.id} className="border rounded p-3 flex items-center justify-between">
                <div>
                  <div className="font-medium">₹ {p.amount}</div>
                  <div className="text-xs text-gray-600">{p.status} · {p.createdAt?.slice(0,10)}</div>
                </div>
                <button className="px-3 py-1.5 rounded bg-gray-100 text-sm">Details</button>
              </div>
            ))}
            {payments.length === 0 && <div className="text-sm text-gray-600">No payments yet.</div>}
          </div>
        </div>
      </div>
    </div>
  )
}



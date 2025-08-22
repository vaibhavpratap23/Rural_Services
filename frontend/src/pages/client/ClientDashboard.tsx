import { useEffect, useState } from 'react'
import axios from 'axios'

type Category = { id: number; name: string }
type Job = { id: number; title: string; status: string; budget?: number }

export default function ClientDashboard() {
  const [jobCategories, setJobCategories] = useState<any>({})
  const [jobs, setJobs] = useState<Job[]>([])
  const [form, setForm] = useState({ 
    title: '', 
    description: '', 
    categoryId: '', 
    budget: '', 
    address: '',
    selectedCategory: '',
    customJob: ''
  })
  const [showCustom, setShowCustom] = useState(false)
  const token = localStorage.getItem('token')

  useEffect(() => {
    // Fetch comprehensive job categories
    axios.get('/api/jobs/categories').then(r => setJobCategories(r.data)).catch(() => {})
    if (token) axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
    axios.get('/api/jobs/me').then(r => setJobs(r.data)).catch(()=>{})
  }, [])

  const postJob = async () => {
    try {
      const jobTitle = showCustom ? form.customJob : form.selectedCategory
      const payload = {
        title: jobTitle || form.title,
        description: form.description,
        categoryId: form.categoryId ? Number(form.categoryId) : undefined,
        budget: form.budget ? Number(form.budget) : undefined,
        address: form.address
      }
      const res = await axios.post('/api/jobs', payload)
      setJobs([res.data, ...jobs])
      setForm({ 
        title: '', 
        description: '', 
        categoryId: '', 
        budget: '', 
        address: '',
        selectedCategory: '',
        customJob: ''
      })
      setShowCustom(false)
    } catch (e: any) {
      alert(e?.response?.data || 'Failed to post job. Make sure you are logged in.')
    }
  }

  return (
    <div className="max-w-6xl mx-auto px-4 py-8 grid grid-cols-1 lg:grid-cols-2 gap-8">
      <div>
        <h2 className="text-xl font-semibold mb-4">Post a Job</h2>
        <div className="space-y-4 bg-white border rounded p-6">
          
          {/* Job Category Selection */}
          <div>
            <label className="block text-sm font-medium mb-2">Select Job Type</label>
            <div className="space-y-4 mb-3">
              {Object.entries(jobCategories).map(([category, jobs]: [string, any]) => (
                <div key={category} className="border rounded-lg p-3">
                  <h4 className="font-medium text-gray-800 mb-2 capitalize">{category}</h4>
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
                    {Array.isArray(jobs) && jobs.slice(0, 6).map((job: string, index: number) => (
                      <button
                        key={index}
                        type="button"
                        onClick={() => setForm({ ...form, selectedCategory: job })}
                        className={`text-sm p-2 border rounded text-left ${
                          form.selectedCategory === job 
                            ? 'bg-blue-100 border-blue-500 text-blue-700' 
                            : 'bg-gray-50 border-gray-300 hover:bg-gray-100'
                        }`}
                      >
                        {job}
                      </button>
                    ))}
                  </div>
                </div>
              ))}
            </div>
            
            <button
              type="button"
              onClick={() => setShowCustom(!showCustom)}
              className="text-sm text-blue-600 underline"
            >
              {showCustom ? 'Choose from categories' : 'Enter custom job description'}
            </button>
          </div>

          {showCustom ? (
            <input 
              className="w-full border rounded px-3 py-2" 
              placeholder="Describe your job requirement" 
              value={form.customJob} 
              onChange={e => setForm({ ...form, customJob: e.target.value })} 
            />
          ) : (
            form.selectedCategory && (
              <div className="text-sm text-green-600 bg-green-50 p-2 rounded">
                ✅ Selected: {form.selectedCategory}
              </div>
            )
          )}

          <textarea 
            className="w-full border rounded px-3 py-2" 
            placeholder="Additional details about the job" 
            value={form.description} 
            onChange={e => setForm({ ...form, description: e.target.value })} 
            rows={3}
          />
          
          <input 
            className="w-full border rounded px-3 py-2" 
            placeholder="Budget (₹)" 
            type="number"
            value={form.budget} 
            onChange={e => setForm({ ...form, budget: e.target.value })} 
          />
          
          <input 
            className="w-full border rounded px-3 py-2" 
            placeholder="Address/Location" 
            value={form.address} 
            onChange={e => setForm({ ...form, address: e.target.value })} 
          />
          
          <button 
            className="w-full bg-blue-600 text-white rounded px-3 py-2 font-medium"
            onClick={postJob}
            disabled={!form.selectedCategory && !form.customJob}
          >
            Post Job
          </button>
        </div>

        {/* Popular Categories */}
        <div className="mt-6">
          <h3 className="text-lg font-medium mb-3">Popular Categories</h3>
          <div className="grid grid-cols-1 gap-2">
            {Object.entries(jobCategories).map(([category, jobs]: [string, any]) => (
              <div key={category} className="bg-gray-50 p-3 rounded">
                <h4 className="font-medium capitalize text-gray-800">{category}</h4>
                <div className="text-sm text-gray-600 mt-1">
                  {Array.isArray(jobs) ? jobs.slice(0, 3).join(', ') : ''}
                  {Array.isArray(jobs) && jobs.length > 3 && '...'}
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      <div>
        <h2 className="text-xl font-semibold mb-4">My Jobs</h2>
        <div className="space-y-3">
          {jobs.map((j: Job) => (
            <div key={j.id} className="bg-white border rounded p-4 flex items-center justify-between">
              <div>
                <div className="font-medium">{j.title}</div>
                <div className="text-sm text-gray-600">
                  Status: <span className={`uppercase font-semibold ${
                    j.status === 'OPEN' ? 'text-green-600' :
                    j.status === 'ASSIGNED' ? 'text-blue-600' :
                    j.status === 'IN_PROGRESS' ? 'text-yellow-600' :
                    j.status === 'COMPLETED' ? 'text-purple-600' : 'text-gray-600'
                  }`}>{j.status}</span>
                </div>
              </div>
              {j.budget != null && <div className="text-sm font-medium">₹ {j.budget}</div>}
            </div>
          ))}
          {jobs.length === 0 && (
            <div className="text-center py-8 text-gray-500">
              <div className="text-lg mb-2">No jobs posted yet</div>
              <div className="text-sm">Post your first job to get started!</div>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}



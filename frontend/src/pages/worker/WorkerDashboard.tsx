import { useEffect, useState } from 'react'
import axios from 'axios'
import JobCard from '../../components/JobCard'
import JobFilters, { JobFilters as JobFiltersType } from '../../components/JobFilters'

type Job = { 
  id: number; 
  title: string; 
  description?: string;
  status: string; 
  budget?: number;
  categoryName?: string;
  address?: string;
  createdAt?: string;
}

export default function WorkerDashboard() {
  const [jobs, setJobs] = useState<Job[]>([])
  const [filteredJobs, setFilteredJobs] = useState<Job[]>([])
  const [myJobs, setMyJobs] = useState<Job[]>([])
  const [activeTab, setActiveTab] = useState<'available' | 'my-jobs'>('available')
  const [categories, setCategories] = useState<string[]>([])

  useEffect(() => {
    const token = localStorage.getItem('token')
    if (token) axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
    
    const fetchJobs = () => {
      // Fetch jobs near worker's location based on their radius
      axios.get('/api/jobs/worker/nearby').then(r => {
        setJobs(r.data)
        setFilteredJobs(r.data)
      }).catch(()=>{})
      axios.get('/api/jobs/me').then(r => setMyJobs(r.data)).catch(()=>{})
      // Fetch categories
      axios.get('/api/jobs/categories').then(r => {
        const allCategories = Object.values(r.data).flat() as string[]
        setCategories(allCategories)
      }).catch(()=>{})
    }
    fetchJobs()
  }, [])

  const refreshJobs = () => {
    axios.get('/api/jobs/worker/nearby').then(r => {
      setJobs(r.data)
      setFilteredJobs(r.data)
    }).catch(()=>{})
    axios.get('/api/jobs/me').then(r => setMyJobs(r.data)).catch(()=>{})
  }

  const handleFilterChange = (filters: JobFiltersType) => {
    let filtered = [...jobs]
    
    // Category filter
    if (filters.category) {
      filtered = filtered.filter(job => job.categoryName === filters.category)
    }
    
    // Budget filter
    filtered = filtered.filter(job => 
      job.budget && job.budget >= filters.minBudget && job.budget <= filters.maxBudget
    )
    
    // Sort
    filtered.sort((a, b) => {
      let comparison = 0
      switch (filters.sortBy) {
        case 'budget':
          comparison = (a.budget || 0) - (b.budget || 0)
          break
        case 'date':
          comparison = new Date(a.createdAt || '').getTime() - new Date(b.createdAt || '').getTime()
          break
        default:
          comparison = 0
      }
      return filters.sortOrder === 'desc' ? -comparison : comparison
    })
    
    setFilteredJobs(filtered)
  }

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-gray-900 mb-2">Worker Dashboard</h1>
        <p className="text-gray-600">Find gigs and manage your work</p>
      </div>

      <div className="mb-6">
        <div className="border-b border-gray-200">
          <nav className="-mb-px flex space-x-8">
            <button
              onClick={() => setActiveTab('available')}
              className={`py-2 px-1 border-b-2 font-medium text-sm ${
                activeTab === 'available'
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              }`}
            >
              Jobs Near Me ({filteredJobs.length})
            </button>
            <button
              onClick={() => setActiveTab('my-jobs')}
              className={`py-2 px-1 border-b-2 font-medium text-sm ${
                activeTab === 'my-jobs'
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              }`}
            >
              My Jobs ({myJobs.length})
            </button>
          </nav>
        </div>
      </div>

      {activeTab === 'available' && (
        <>
          <JobFilters onFilterChange={handleFilterChange} categories={categories} />
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {filteredJobs.map(job => (
              <JobCard key={job.id} job={job} onUpdate={refreshJobs} />
            ))}
            {filteredJobs.length === 0 && (
              <div className="col-span-full text-center py-12">
                <div className="text-gray-500">No jobs match your filters</div>
                <div className="text-sm text-gray-400 mt-2">Try adjusting your search criteria</div>
              </div>
            )}
          </div>
        </>
      )}

      {activeTab === 'my-jobs' && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {myJobs.map(job => (
            <JobCard key={job.id} job={job} onUpdate={refreshJobs} showActions={false} />
          ))}
          {myJobs.length === 0 && (
            <div className="col-span-full text-center py-12">
              <div className="text-gray-500">You haven't accepted any jobs yet</div>
              <div className="text-sm text-gray-400 mt-2">Browse available gigs to get started</div>
            </div>
          )}
        </div>
      )}
    </div>
  )
}



import { useState } from 'react'
import axios from 'axios'
import { useToast } from '../hooks/useToast'
import RatingModal from './RatingModal'

type JobCardProps = {
  job: {
    id: number
    title: string
    description?: string
    budget?: number
    status: string
    categoryName?: string
    address?: string
    createdAt?: string
  }
  onUpdate?: () => void
  showActions?: boolean
}

export default function JobCard({ job, onUpdate, showActions = true }: JobCardProps) {
  const [loading, setLoading] = useState(false)
  const [showRatingModal, setShowRatingModal] = useState(false)
  const { success, error } = useToast()

  const acceptJob = async () => {
    setLoading(true)
    try {
      await axios.put(`/api/jobs/${job.id}/accept`)
      success('Job Accepted', 'You have successfully accepted this job')
      onUpdate?.()
    } catch (e: any) {
      error('Failed to Accept', e?.response?.data || 'Failed to accept job')
    } finally {
      setLoading(false)
    }
  }

  const startJob = async () => {
    setLoading(true)
    try {
      await axios.put(`/api/jobs/${job.id}/start`)
      success('Job Started', 'You have started working on this job')
      onUpdate?.()
    } catch (e: any) {
      error('Failed to Start', e?.response?.data || 'Failed to start job')
    } finally {
      setLoading(false)
    }
  }

  const completeJob = async () => {
    setLoading(true)
    try {
      await axios.put(`/api/jobs/${job.id}/complete`)
      success('Job Completed', 'Job marked as completed successfully')
      onUpdate?.()
    } catch (e: any) {
      error('Failed to Complete', e?.response?.data || 'Failed to complete job')
    } finally {
      setLoading(false)
    }
  }

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'OPEN': return 'bg-green-100 text-green-800'
      case 'ASSIGNED': return 'bg-blue-100 text-blue-800'
      case 'IN_PROGRESS': return 'bg-yellow-100 text-yellow-800'
      case 'COMPLETED': return 'bg-gray-100 text-gray-800'
      default: return 'bg-gray-100 text-gray-800'
    }
  }

  const getActionButton = () => {
    if (!showActions) return null
    
    switch (job.status) {
      case 'OPEN':
        return (
          <button 
            onClick={acceptJob} 
            disabled={loading}
            className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50"
          >
            {loading ? 'Accepting...' : 'Accept Job'}
          </button>
        )
      case 'ASSIGNED':
        return (
          <button 
            onClick={startJob} 
            disabled={loading}
            className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 disabled:opacity-50"
          >
            {loading ? 'Starting...' : 'Start Job'}
          </button>
        )
      case 'IN_PROGRESS':
        return (
          <button 
            onClick={completeJob} 
            disabled={loading}
            className="px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 disabled:opacity-50"
          >
            {loading ? 'Completing...' : 'Complete Job'}
          </button>
        )
      case 'COMPLETED':
        return (
          <button 
            onClick={() => setShowRatingModal(true)} 
            className="px-4 py-2 bg-yellow-600 text-white rounded-lg hover:bg-yellow-700"
          >
            Rate Job
          </button>
        )
      default:
        return null
    }
  }

  return (
    <div className="bg-white rounded-lg shadow-md border border-gray-200 p-6 hover:shadow-lg transition-shadow">
      <div className="flex items-start justify-between mb-4">
        <div className="flex-1">
          <h3 className="text-lg font-semibold text-gray-900 mb-2">{job.title}</h3>
          {job.description && (
            <p className="text-gray-600 text-sm mb-3 line-clamp-2">{job.description}</p>
          )}
        </div>
        <span className={`px-3 py-1 rounded-full text-xs font-medium ${getStatusColor(job.status)}`}>
          {job.status.replace('_', ' ')}
        </span>
      </div>

      <div className="space-y-2 mb-4">
        {job.categoryName && (
          <div className="flex items-center text-sm text-gray-600">
            <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
            </svg>
            {job.categoryName}
          </div>
        )}
        {job.address && (
          <div className="flex items-center text-sm text-gray-600">
            <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
            </svg>
            {job.address}
          </div>
        )}
        {job.createdAt && (
          <div className="flex items-center text-sm text-gray-600">
            <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
            {new Date(job.createdAt).toLocaleDateString()}
          </div>
        )}
      </div>

      <div className="flex items-center justify-between">
        <div className="flex items-center">
          {job.budget && (
            <div className="text-lg font-bold text-green-600">
              ₹{job.budget.toLocaleString()}
            </div>
          )}
        </div>
        {getActionButton()}
      </div>
      <RatingModal 
        jobId={job.id}
        isOpen={showRatingModal}
        onClose={() => setShowRatingModal(false)}
        onRatingSubmitted={() => onUpdate?.()}
      />
    </div>
  )
}

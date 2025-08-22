import { useEffect, useState } from 'react'
import axios from 'axios'

interface Document {
  id: number
  type: string
  fileUrl: string
  verificationStatus: string
}

interface Worker {
  id: number
  name: string
  email: string
  phone: string
  verificationStatus: string
  aadhaarNumber?: string
  panNumber?: string
  address?: string
  documents: Document[]
}

export default function WorkerVerification() {
  const [workers, setWorkers] = useState<Worker[]>([])
  const [loading, setLoading] = useState(true)
  const [selectedWorker, setSelectedWorker] = useState<Worker | null>(null)
  const [rejectReason, setRejectReason] = useState('')
  const [showRejectModal, setShowRejectModal] = useState(false)

  useEffect(() => {
    fetchPendingWorkers()
  }, [])

  const fetchPendingWorkers = async () => {
    try {
      const token = localStorage.getItem('adminToken')
      if (token) {
        axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
      }

      const response = await axios.get('/api/admin/workers/pending')
      setWorkers(response.data)
    } catch (error) {
      console.error('Failed to fetch pending workers:', error)
    } finally {
      setLoading(false)
    }
  }

  const approveWorker = async (workerId: number) => {
    try {
      await axios.post(`/api/admin/workers/${workerId}/approve`)
      setWorkers(workers.filter(w => w.id !== workerId))
      setSelectedWorker(null)
      alert('Worker approved successfully!')
    } catch (error) {
      console.error('Failed to approve worker:', error)
      alert('Failed to approve worker')
    }
  }

  const rejectWorker = async (workerId: number) => {
    try {
      await axios.post(`/api/admin/workers/${workerId}/reject`, {
        reason: rejectReason
      })
      setWorkers(workers.filter(w => w.id !== workerId))
      setSelectedWorker(null)
      setShowRejectModal(false)
      setRejectReason('')
      alert('Worker rejected successfully!')
    } catch (error) {
      console.error('Failed to reject worker:', error)
      alert('Failed to reject worker')
    }
  }

  const getStatusBadge = (status: string) => {
    const baseClasses = "px-2 py-1 text-xs font-medium rounded-full"
    switch (status) {
      case 'PENDING_BASIC':
        return `${baseClasses} bg-yellow-100 text-yellow-800`
      case 'PENDING_FULL':
        return `${baseClasses} bg-blue-100 text-blue-800`
      case 'VERIFIED':
        return `${baseClasses} bg-green-100 text-green-800`
      case 'REJECTED':
        return `${baseClasses} bg-red-100 text-red-800`
      default:
        return `${baseClasses} bg-gray-100 text-gray-800`
    }
  }

  const getDocumentTypeLabel = (type: string) => {
    switch (type) {
      case 'AADHAAR_CARD': return 'Aadhaar Card'
      case 'PAN_CARD': return 'PAN Card'
      case 'DRIVING_LICENSE': return 'Driving License'
      case 'SELFIE_WITH_AADHAAR': return 'Selfie with Aadhaar'
      default: return type.replace('_', ' ')
    }
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-gray-500">Loading pending workers...</div>
      </div>
    )
  }

  return (
    <div className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <div className="px-4 py-6 sm:px-0">
        <h1 className="text-2xl font-bold text-gray-900 mb-8">Worker Verification</h1>
        
        {workers.length === 0 ? (
          <div className="text-center py-12">
            <div className="text-gray-500 text-lg">No workers pending verification</div>
            <div className="text-gray-400 text-sm mt-2">All workers have been processed</div>
          </div>
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
            {/* Workers List */}
            <div className="bg-white shadow rounded-lg">
              <div className="px-4 py-5 sm:p-6">
                <h3 className="text-lg font-medium text-gray-900 mb-4">
                  Pending Workers ({workers.length})
                </h3>
                <div className="space-y-3">
                  {workers.map(worker => (
                    <div
                      key={worker.id}
                      className={`p-4 border rounded-lg cursor-pointer transition-colors ${
                        selectedWorker?.id === worker.id
                          ? 'border-blue-500 bg-blue-50'
                          : 'border-gray-200 hover:border-gray-300'
                      }`}
                      onClick={() => setSelectedWorker(worker)}
                    >
                      <div className="flex items-center justify-between">
                        <div>
                          <div className="font-medium text-gray-900">{worker.name}</div>
                          <div className="text-sm text-gray-500">{worker.email}</div>
                          <div className="text-sm text-gray-500">{worker.phone}</div>
                        </div>
                        <div className="text-right">
                          <div className={getStatusBadge(worker.verificationStatus)}>
                            {worker.verificationStatus.replace('PENDING_', '')}
                          </div>
                          <div className="text-xs text-gray-400 mt-1">
                            {worker.documents.length} documents
                          </div>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>

            {/* Worker Details */}
            <div className="bg-white shadow rounded-lg">
              <div className="px-4 py-5 sm:p-6">
                {selectedWorker ? (
                  <div>
                    <div className="flex items-center justify-between mb-6">
                      <h3 className="text-lg font-medium text-gray-900">
                        Worker Details
                      </h3>
                      <div className={getStatusBadge(selectedWorker.verificationStatus)}>
                        {selectedWorker.verificationStatus.replace('PENDING_', '')}
                      </div>
                    </div>

                    {/* Personal Information */}
                    <div className="mb-6">
                      <h4 className="text-md font-medium text-gray-900 mb-3">Personal Information</h4>
                      <div className="grid grid-cols-1 gap-3 text-sm">
                        <div><span className="font-medium">Name:</span> {selectedWorker.name}</div>
                        <div><span className="font-medium">Email:</span> {selectedWorker.email}</div>
                        <div><span className="font-medium">Phone:</span> {selectedWorker.phone}</div>
                        {selectedWorker.aadhaarNumber && (
                          <div><span className="font-medium">Aadhaar:</span> {selectedWorker.aadhaarNumber}</div>
                        )}
                        {selectedWorker.panNumber && (
                          <div><span className="font-medium">PAN:</span> {selectedWorker.panNumber}</div>
                        )}
                        {selectedWorker.address && (
                          <div><span className="font-medium">Address:</span> {selectedWorker.address}</div>
                        )}
                      </div>
                    </div>

                    {/* Documents */}
                    <div className="mb-6">
                      <h4 className="text-md font-medium text-gray-900 mb-3">Documents</h4>
                      <div className="space-y-3">
                        {selectedWorker.documents.map(doc => (
                          <div key={doc.id} className="border rounded-lg p-3">
                            <div className="flex items-center justify-between mb-2">
                              <span className="font-medium text-sm">
                                {getDocumentTypeLabel(doc.type)}
                              </span>
                              <div className={getStatusBadge(doc.verificationStatus)}>
                                {doc.verificationStatus}
                              </div>
                            </div>
                            <a
                              href={`/api/files/${doc.fileUrl}`}
                              target="_blank"
                              rel="noopener noreferrer"
                              className="text-blue-600 hover:text-blue-800 text-sm underline"
                            >
                              View Document
                            </a>
                          </div>
                        ))}
                      </div>
                    </div>

                    {/* Actions */}
                    <div className="flex space-x-3">
                      <button
                        onClick={() => approveWorker(selectedWorker.id)}
                        className="flex-1 bg-green-600 text-white px-4 py-2 rounded-md hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-green-500"
                      >
                        Approve Worker
                      </button>
                      <button
                        onClick={() => setShowRejectModal(true)}
                        className="flex-1 bg-red-600 text-white px-4 py-2 rounded-md hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500"
                      >
                        Reject Worker
                      </button>
                    </div>
                  </div>
                ) : (
                  <div className="text-center py-12 text-gray-500">
                    Select a worker to view details
                  </div>
                )}
              </div>
            </div>
          </div>
        )}

        {/* Reject Modal */}
        {showRejectModal && (
          <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
            <div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
              <div className="mt-3">
                <h3 className="text-lg font-medium text-gray-900 mb-4">
                  Reject Worker
                </h3>
                <div className="mb-4">
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Reason for rejection:
                  </label>
                  <textarea
                    className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-red-500"
                    rows={3}
                    value={rejectReason}
                    onChange={e => setRejectReason(e.target.value)}
                    placeholder="Please provide a reason for rejection..."
                  />
                </div>
                <div className="flex space-x-3">
                  <button
                    onClick={() => {
                      setShowRejectModal(false)
                      setRejectReason('')
                    }}
                    className="flex-1 bg-gray-300 text-gray-700 px-4 py-2 rounded-md hover:bg-gray-400"
                  >
                    Cancel
                  </button>
                  <button
                    onClick={() => selectedWorker && rejectWorker(selectedWorker.id)}
                    disabled={!rejectReason.trim()}
                    className="flex-1 bg-red-600 text-white px-4 py-2 rounded-md hover:bg-red-700 disabled:opacity-50"
                  >
                    Reject
                  </button>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}

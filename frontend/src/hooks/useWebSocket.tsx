import { useEffect, useRef, useState } from 'react'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { useToast } from './useToast'

interface Notification {
  type: string
  data: any
  timestamp: number
}

export function useWebSocket(userId?: string) {
  const [client, setClient] = useState<Client | null>(null)
  const [connected, setConnected] = useState(false)
  const { info, success } = useToast()
  const clientRef = useRef<Client | null>(null)

  useEffect(() => {
    if (!userId) return

    const stompClient = new Client({
      webSocketFactory: () => new SockJS('/ws'),
      onConnect: () => {
        setConnected(true)
        
        // Subscribe to user-specific notifications
        stompClient.subscribe(`/topic/user/${userId}`, (message) => {
          const notification: Notification = JSON.parse(message.body)
          handleNotification(notification)
        })
      },
      onDisconnect: () => {
        setConnected(false)
      },
      onStompError: (frame) => {
        console.error('STOMP error:', frame)
      }
    })

    stompClient.activate()
    setClient(stompClient)
    clientRef.current = stompClient

    return () => {
      if (clientRef.current) {
        clientRef.current.deactivate()
      }
    }
  }, [userId])

  const handleNotification = (notification: Notification) => {
    switch (notification.type) {
      case 'JOB_ACCEPTED':
        success('Job Accepted!', notification.data.message)
        break
      case 'JOB_STARTED':
        info('Job Started', notification.data.message)
        break
      case 'JOB_COMPLETED':
        success('Job Completed!', notification.data.message)
        break
      case 'NEW_JOB':
        info('New Job Available', notification.data.message)
        break
      default:
        info('Notification', notification.data.message || 'You have a new notification')
    }
  }

  return { client, connected }
}

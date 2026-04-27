import { getAction, putAction } from '@/api/manage'

/**
 * 资产登记申请 API
 */
export function getRegistrationList(params) {
  return getAction('/assetRegistration/list', params)
}

export function getRegistrationByAssetId(assetId) {
  return getAction('/assetRegistration/queryByAssetId', { assetId })
}

export function getPendingRegistrations(spaceId) {
  return getAction('/assetRegistration/queryPendingBySpaceId', { spaceId })
}

export function getRegistrationById(id) {
  return getAction('/assetRegistration/queryById', { id })
}

export function approveRegistration(id) {
  return putAction('/assetRegistration/approve', { id })
}

export function rejectRegistration(id) {
  return putAction('/assetRegistration/reject', { id })
}

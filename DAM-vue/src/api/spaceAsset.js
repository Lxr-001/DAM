import { getAction, postAction, putAction, deleteAction, uploadAction } from '@/api/manage'

/**
 * 数据资产主表 API
 */
export function getSpaceAssetList(params) {
  return getAction('/spaceAsset/queryAssetList', params)
}

export function getSpaceAssetPageList(params) {
  return getAction('/spaceAsset/list', params)
}

export function getSpaceAssetById(id) {
  return getAction('/spaceAsset/queryById', { id })
}

export function addSpaceAsset(data, approverId) {
  return postAction('/spaceAsset/add?approverId=' + approverId, data)
}

export function editSpaceAsset(data) {
  return putAction('/spaceAsset/edit', data)
}

export function deleteSpaceAsset(id) {
  return deleteAction('/spaceAsset/delete', { id })
}

export function deleteBatchSpaceAsset(ids) {
  return deleteAction('/spaceAsset/deleteBatch', { ids })
}

/**
 * 提交转让登记
 */
export function submitTransfer(params) {
  return postAction('/spaceAsset/submitTransfer', params)
}

/**
 * 提交变更登记
 */
export function submitChange(data, approverId) {
  return putAction('/spaceAsset/submitChange', { ...data, approverId })
}

/**
 * 提交注销登记
 */
export function submitCancel(params) {
  return postAction('/spaceAsset/submitCancel', params)
}

/**
 * 更新证书信息
 */
export function updateCert(params) {
  return putAction('/spaceAsset/updateCert', params)
}

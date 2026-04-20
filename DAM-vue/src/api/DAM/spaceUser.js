import { getAction, postAction, deleteAction, httpAction } from '@/api/manage'

export function getSpaceUserList(params) {
  return getAction('/spaceUser/list', params)
}

export function getSpaceUserListBySpaceId(params) {
  return getAction('/spaceUser/listBySpaceId', params)
}

export function addSpaceUser(params) {
  return postAction('/spaceUser/add', params)
}

export function addSpaceUserBatch(params) {
  return httpAction('/spaceUser/addBatch', params, 'post')
}

export function editSpaceUser(params) {
  return httpAction('/spaceUser/edit', params, 'put')
}

export function deleteSpaceUser(params) {
  return deleteAction('/spaceUser/delete', params)
}

export function deleteSpaceUserBatch(params) {
  return deleteAction('/spaceUser/deleteBatch', params)
}

export function removeUserFromSpace(params) {
  return deleteAction('/spaceUser/removeUser', params)
}

export function queryByIdSpaceUser(params) {
  return getAction('/spaceUser/queryById', params)
}
import { postAction } from '../manage.js'

// 数据共享相关的API接口
const api = {
  submitShareRequest: '/akDataShare/sysDataShare/add',
  approveShareRequest: '/akDataShare/sysDataShare/approve',
  rejectShareRequest: '/akDataShare/sysDataShare/reject',
  cancelShareRequest: '/akDataShare/sysDataShare/cancel',
  getShareHistory: '/akDataShare/sysDataShare/history',
  getPendingApprovals: '/akDataShare/sysDataShare/pending'
}

/**
 * 提交数据共享申请
 * @param {Object} parameter - 请求参数
 * @param {string} parameter.dataId - 数据ID
 * @param {string} parameter.moduleCode - 模块代码
 * @param {string} parameter.approvers - 审批人ID列表，逗号分隔
 * @param {string} parameter.approversName - 审批人名称列表，逗号分隔
 * @param {string} parameter.shareReason - 共享原因
 * @returns {Promise} - 返回Promise对象
 */
export function submitShareRequest(parameter) {
  return postAction(api.submitShareRequest, parameter)
}

/**
 * 审批数据共享申请
 * @param {Object} parameter - 请求参数
 * @param {string} parameter.id - 共享记录ID
 * @param {string} parameter.approveResult - 审批结果（approve/reject）
 * @param {string} parameter.approveComment - 审批意见
 * @returns {Promise} - 返回Promise对象
 */
export function approveShareRequest(parameter) {
  return postAction(api.approveShareRequest, parameter)
}

/**
 * 拒绝数据共享申请
 * @param {Object} parameter - 请求参数
 * @param {string} parameter.id - 共享记录ID
 * @param {string} parameter.rejectReason - 拒绝原因
 * @returns {Promise} - 返回Promise对象
 */
export function rejectShareRequest(parameter) {
  return postAction(api.rejectShareRequest, parameter)
}

/**
 * 取消数据共享申请
 * @param {Object} parameter - 请求参数
 * @param {string} parameter.id - 共享记录ID
 * @returns {Promise} - 返回Promise对象
 */
export function cancelShareRequest(parameter) {
  return postAction(api.cancelShareRequest, parameter)
}

/**
 * 获取共享历史记录
 * @param {Object} parameter - 请求参数
 * @param {string} parameter.dataId - 数据ID
 * @param {string} parameter.moduleCode - 模块代码
 * @returns {Promise} - 返回Promise对象
 */
export function getShareHistory(parameter) {
  return postAction(api.getShareHistory, parameter)
}

/**
 * 获取待审批列表
 * @param {Object} parameter - 请求参数
 * @param {string} parameter.userId - 用户ID
 * @returns {Promise} - 返回Promise对象
 */
export function getPendingApprovals(parameter) {
  return postAction(api.getPendingApprovals, parameter)
}
import React, { useState } from 'react';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import OrganizationTree from './components/OrganizationTree';
import UserList from './components/UserList';

export interface Organization {
  id: string;
  code: string;
  name: string;
  children?: Organization[];
}

export interface User {
  id: string;
  username: string;
  position: string;
  status: '正常' | '锁定' | '停用';
  roles: string[];
  securityLevel: '绝密' | '机密' | '内部' | '公开';
  effectiveDate: string;
  expiryDate: string;
  orgId: string;
}

// Mock 数据
const mockOrganizations: Organization[] = [
  {
    id: '1',
    code: 'ORG001',
    name: '总公司',
    children: [
      {
        id: '1-1',
        code: 'ORG001-001',
        name: '技术部',
        children: [
          { id: '1-1-1', code: 'ORG001-001-001', name: '前端组' },
          { id: '1-1-2', code: 'ORG001-001-002', name: '后端组' },
          { id: '1-1-3', code: 'ORG001-001-003', name: '测试组' }
        ]
      },
      {
        id: '1-2',
        code: 'ORG001-002',
        name: '产品部',
        children: [
          { id: '1-2-1', code: 'ORG001-002-001', name: '产品一组' },
          { id: '1-2-2', code: 'ORG001-002-002', name: '产品二组' }
        ]
      },
      {
        id: '1-3',
        code: 'ORG001-003',
        name: '人力资源部'
      }
    ]
  },
  {
    id: '2',
    code: 'ORG002',
    name: '分公司',
    children: [
      { id: '2-1', code: 'ORG002-001', name: '上海分公司' },
      { id: '2-2', code: 'ORG002-002', name: '北京分公司' }
    ]
  }
];

const mockUsers: User[] = [
  {
    id: '1',
    username: '张三',
    position: '前端工程师',
    status: '正常',
    roles: ['数据使用者'],
    securityLevel: '公开',
    effectiveDate: '2024-01-01',
    expiryDate: '2026-12-31',
    orgId: '1-1-1'
  },
  {
    id: '2',
    username: '李四',
    position: '高级前端工程师',
    status: '正常',
    roles: ['数据管理员', '数据使用者'],
    securityLevel: '内部',
    effectiveDate: '2023-06-01',
    expiryDate: '2027-06-01',
    orgId: '1-1-1'
  },
  {
    id: '3',
    username: '王五',
    position: '后端工程师',
    status: '正常',
    roles: ['数据使用者'],
    securityLevel: '公开',
    effectiveDate: '2024-03-01',
    expiryDate: '2026-12-31',
    orgId: '1-1-2'
  },
  {
    id: '4',
    username: '赵六',
    position: '测试工程师',
    status: '正常',
    roles: ['数据使用者'],
    securityLevel: '公开',
    effectiveDate: '2024-02-15',
    expiryDate: '2026-12-31',
    orgId: '1-1-3'
  },
  {
    id: '5',
    username: '孙七',
    position: '产品经理',
    status: '正常',
    roles: ['数据空间管理员', '数据Owner'],
    securityLevel: '机密',
    effectiveDate: '2023-01-01',
    expiryDate: '2027-12-31',
    orgId: '1-2-1'
  },
  {
    id: '6',
    username: '周八',
    position: 'HR主管',
    status: '正常',
    roles: ['数据管理员', '数据管家'],
    securityLevel: '机密',
    effectiveDate: '2022-06-01',
    expiryDate: '2027-12-31',
    orgId: '1-3'
  },
  {
    id: '7',
    username: '吴九',
    position: '前端实习生',
    status: '停用',
    roles: ['数据使用者'],
    securityLevel: '公开',
    effectiveDate: '2024-07-01',
    expiryDate: '2024-12-31',
    orgId: '1-1-1'
  }
];

export default function App() {
  const [organizations, setOrganizations] = useState<Organization[]>(mockOrganizations);
  const [users, setUsers] = useState<User[]>(mockUsers);
  const [selectedOrgId, setSelectedOrgId] = useState<string>('1-1-1');

  return (
    <ConfigProvider locale={zhCN}>
      <div className="flex h-screen bg-gray-50">
        {/* 左侧组织架构树 */}
        <div className="w-80 bg-white border-r border-gray-200">
          <OrganizationTree
            organizations={organizations}
            setOrganizations={setOrganizations}
            selectedOrgId={selectedOrgId}
            onSelectOrg={setSelectedOrgId}
          />
        </div>

        {/* 右侧用户列表 */}
        <div className="flex-1 overflow-hidden">
          <UserList
            users={users}
            setUsers={setUsers}
            selectedOrgId={selectedOrgId}
          />
        </div>
      </div>
    </ConfigProvider>
  );
}

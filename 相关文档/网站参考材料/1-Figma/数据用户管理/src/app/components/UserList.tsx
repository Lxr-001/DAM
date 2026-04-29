import React, { useState, useMemo } from 'react';
import { Table, Input, Button, Space, Tag, Modal, message, Dropdown } from 'antd';
import type { ColumnsType, TablePaginationConfig } from 'antd/es/table';
import {
  SearchOutlined,
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SettingOutlined,
  MoreOutlined
} from '@ant-design/icons';
import type { User } from '../App';
import UserDrawer from './UserDrawer';

const { Search } = Input;

interface UserListProps {
  users: User[];
  setUsers: (users: User[]) => void;
  selectedOrgId: string;
}

const statusColorMap = {
  '正常': 'success',
  '锁定': 'warning',
  '停用': 'error'
} as const;

const securityLevelColorMap = {
  '绝密': 'red',
  '机密': 'orange',
  '内部': 'blue',
  '公开': 'green'
} as const;

export default function UserList({ users, setUsers, selectedOrgId }: UserListProps) {
  const [searchValue, setSearchValue] = useState('');
  const [drawerVisible, setDrawerVisible] = useState(false);
  const [editingUser, setEditingUser] = useState<User | null>(null);
  const [pagination, setPagination] = useState<TablePaginationConfig>({
    current: 1,
    pageSize: 10,
    total: 0,
    showSizeChanger: true,
    showQuickJumper: true,
    showTotal: (total) => `共 ${total} 条`,
    pageSizeOptions: ['10', '20', '50', '100']
  });
  const [visibleColumns, setVisibleColumns] = useState<string[]>([
    'username',
    'position',
    'status',
    'roles',
    'securityLevel',
    'effectiveDate',
    'expiryDate',
    'actions'
  ]);

  // 过滤当前组织的用户
  const filteredUsers = useMemo(() => {
    let result = users.filter(user => user.orgId === selectedOrgId);
    
    if (searchValue) {
      result = result.filter(user =>
        user.username.toLowerCase().includes(searchValue.toLowerCase()) ||
        user.position.toLowerCase().includes(searchValue.toLowerCase())
      );
    }
    
    return result;
  }, [users, selectedOrgId, searchValue]);

  // 定义所有列
  const allColumns: ColumnsType<User> = [
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username',
      width: 120,
      fixed: 'left'
    },
    {
      title: '岗位/职务',
      dataIndex: 'position',
      key: 'position',
      width: 150
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: User['status']) => (
        <Tag color={statusColorMap[status]}>{status}</Tag>
      )
    },
    {
      title: '角色',
      dataIndex: 'roles',
      key: 'roles',
      width: 250,
      render: (roles: string[]) => (
        <Space size={[0, 4]} wrap>
          {roles.map(role => (
            <Tag key={role} color="blue">{role}</Tag>
          ))}
        </Space>
      )
    },
    {
      title: '安全等级',
      dataIndex: 'securityLevel',
      key: 'securityLevel',
      width: 100,
      render: (level: User['securityLevel']) => (
        <Tag color={securityLevelColorMap[level]}>{level}</Tag>
      )
    },
    {
      title: '权限生效日期',
      dataIndex: 'effectiveDate',
      key: 'effectiveDate',
      width: 130
    },
    {
      title: '权限过期日期',
      dataIndex: 'expiryDate',
      key: 'expiryDate',
      width: 130
    },
    {
      title: '操作',
      key: 'actions',
      width: 100,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
          >
            编辑
          </Button>
          <Button
            type="link"
            danger
            size="small"
            icon={<DeleteOutlined />}
            onClick={() => handleDelete(record.id)}
          >
            删除
          </Button>
        </Space>
      )
    }
  ];

  // 根据visibleColumns筛选显示的列
  const displayColumns = allColumns.filter(col => 
    visibleColumns.includes(col.key as string)
  );

  // 列配置选项
  const columnItems = allColumns
    .filter(col => col.key !== 'actions')
    .map(col => ({
      key: col.key as string,
      label: (
        <div className="flex items-center">
          <input
            type="checkbox"
            checked={visibleColumns.includes(col.key as string)}
            onChange={(e) => {
              if (e.target.checked) {
                setVisibleColumns([...visibleColumns, col.key as string]);
              } else {
                setVisibleColumns(visibleColumns.filter(k => k !== col.key));
              }
            }}
            className="mr-2"
          />
          {col.title as string}
        </div>
      )
    }));

  // 新增用户
  const handleAdd = () => {
    setEditingUser(null);
    setDrawerVisible(true);
  };

  // 编辑用户
  const handleEdit = (user: User) => {
    setEditingUser(user);
    setDrawerVisible(true);
  };

  // 删除用户
  const handleDelete = (id: string) => {
    Modal.confirm({
      title: '确认删除',
      content: '确定要删除该用户吗？',
      okText: '确定',
      cancelText: '取消',
      onOk: () => {
        setUsers(users.filter(user => user.id !== id));
        message.success('删除成功');
      }
    });
  };

  // 保存用户
  const handleSave = (user: User) => {
    if (editingUser) {
      // 编辑模式
      setUsers(users.map(u => (u.id === user.id ? user : u)));
      message.success('编辑成功');
    } else {
      // 新增模式
      const newUser = {
        ...user,
        id: `user-${Date.now()}`,
        orgId: selectedOrgId
      };
      setUsers([...users, newUser]);
      message.success('新增成功');
    }
    setDrawerVisible(false);
  };

  return (
    <div className="h-full flex flex-col bg-white">
      {/* 头部 */}
      <div className="p-4 border-b border-gray-200">
        <div className="flex items-center justify-between mb-3">
          <h2 className="text-lg">用户列表</h2>
        </div>
        <div className="flex items-center justify-between">
          <Search
            placeholder="搜索用户名或岗位"
            prefix={<SearchOutlined />}
            allowClear
            value={searchValue}
            onChange={(e) => setSearchValue(e.target.value)}
            style={{ width: 300 }}
          />
          <Space>
            <Dropdown
              menu={{ items: columnItems }}
              trigger={['click']}
            >
              <Button icon={<SettingOutlined />}>
                列配置
              </Button>
            </Dropdown>
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={handleAdd}
            >
              新增用户
            </Button>
          </Space>
        </div>
      </div>

      {/* 表格 */}
      <div className="flex-1 overflow-hidden p-4">
        <Table
          columns={displayColumns}
          dataSource={filteredUsers}
          rowKey="id"
          pagination={pagination}
          onChange={(newPagination) => setPagination(newPagination)}
          scroll={{ x: 1300, y: 'calc(100vh - 280px)' }}
        />
      </div>

      {/* 新增/编辑抽屉 */}
      <UserDrawer
        visible={drawerVisible}
        onClose={() => setDrawerVisible(false)}
        onSave={handleSave}
        user={editingUser}
      />
    </div>
  );
}

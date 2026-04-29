import React, { useState, useMemo } from 'react';
import { Tree, Input, Button, Modal, Form, message, Dropdown, Space } from 'antd';
import type { TreeDataNode } from 'antd';
import {
  SearchOutlined,
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  MoreOutlined,
  FolderOutlined,
  FolderOpenOutlined
} from '@ant-design/icons';
import type { Organization } from '../App';

const { Search } = Input;

interface OrganizationTreeProps {
  organizations: Organization[];
  setOrganizations: (orgs: Organization[]) => void;
  selectedOrgId: string;
  onSelectOrg: (id: string) => void;
}

export default function OrganizationTree({
  organizations,
  setOrganizations,
  selectedOrgId,
  onSelectOrg
}: OrganizationTreeProps) {
  const [searchValue, setSearchValue] = useState('');
  const [expandedKeys, setExpandedKeys] = useState<string[]>(['1', '1-1', '1-2']);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingOrg, setEditingOrg] = useState<Organization | null>(null);
  const [form] = Form.useForm();

  // 转换为Tree组件数据格式
  const convertToTreeData = (orgs: Organization[]): TreeDataNode[] => {
    return orgs.map(org => ({
      key: org.id,
      title: renderTreeNode(org),
      icon: expandedKeys.includes(org.id) ? <FolderOpenOutlined /> : <FolderOutlined />,
      children: org.children ? convertToTreeData(org.children) : undefined
    }));
  };

  // 渲染树节点
  const renderTreeNode = (org: Organization) => {
    const index = org.name.toLowerCase().indexOf(searchValue.toLowerCase());
    const beforeStr = org.name.substring(0, index);
    const matchStr = org.name.substring(index, index + searchValue.length);
    const afterStr = org.name.substring(index + searchValue.length);
    
    const title = index > -1 && searchValue ? (
      <span>
        {beforeStr}
        <span className="text-red-500 bg-yellow-100">{matchStr}</span>
        {afterStr}
      </span>
    ) : (
      <span>{org.name}</span>
    );

    return (
      <div className="flex items-center justify-between w-full pr-2 group">
        <span>{title}</span>
        <Dropdown
          menu={{
            items: [
              {
                key: 'add',
                icon: <PlusOutlined />,
                label: '新增子组织',
                onClick: () => handleAddChild(org)
              },
              {
                key: 'edit',
                icon: <EditOutlined />,
                label: '编辑',
                onClick: () => handleEdit(org)
              },
              {
                key: 'delete',
                icon: <DeleteOutlined />,
                label: '删除',
                danger: true,
                onClick: () => handleDelete(org.id)
              }
            ]
          }}
          trigger={['click']}
        >
          <Button
            type="text"
            size="small"
            icon={<MoreOutlined />}
            className="opacity-0 group-hover:opacity-100"
            onClick={(e) => e.stopPropagation()}
          />
        </Dropdown>
      </div>
    );
  };

  // 过滤树数据
  const getFilteredTreeData = useMemo(() => {
    if (!searchValue) return convertToTreeData(organizations);

    const filterOrg = (org: Organization): Organization | null => {
      const isMatch = org.name.toLowerCase().includes(searchValue.toLowerCase());
      const filteredChildren = org.children
        ?.map(child => filterOrg(child))
        .filter((child): child is Organization => child !== null);

      if (isMatch || (filteredChildren && filteredChildren.length > 0)) {
        return {
          ...org,
          children: filteredChildren
        };
      }
      return null;
    };

    const filtered = organizations
      .map(org => filterOrg(org))
      .filter((org): org is Organization => org !== null);
    
    return convertToTreeData(filtered);
  }, [organizations, searchValue, expandedKeys, selectedOrgId]);

  // 添加子组织
  const handleAddChild = (parent: Organization) => {
    setEditingOrg({ id: '', code: '', name: '', children: [] });
    form.setFieldsValue({ parentId: parent.id, parentName: parent.name });
    setIsModalOpen(true);
  };

  // 添加根组织
  const handleAddRoot = () => {
    setEditingOrg({ id: '', code: '', name: '', children: [] });
    form.resetFields();
    setIsModalOpen(true);
  };

  // 编辑组织
  const handleEdit = (org: Organization) => {
    setEditingOrg(org);
    form.setFieldsValue({ code: org.code, name: org.name });
    setIsModalOpen(true);
  };

  // 删除组织
  const handleDelete = (id: string) => {
    Modal.confirm({
      title: '确认删除',
      content: '删除组织将同时删除其所有子组织，确定要删除吗？',
      okText: '确定',
      cancelText: '取消',
      onOk: () => {
        const deleteOrgRecursive = (orgs: Organization[]): Organization[] => {
          return orgs.filter(org => {
            if (org.id === id) return false;
            if (org.children) {
              org.children = deleteOrgRecursive(org.children);
            }
            return true;
          });
        };
        setOrganizations(deleteOrgRecursive([...organizations]));
        message.success('删除成功');
      }
    });
  };

  // 保存组织
  const handleSave = async () => {
    try {
      const values = await form.validateFields();
      const newId = editingOrg?.id || `new-${Date.now()}`;
      const newOrg: Organization = {
        id: newId,
        code: values.code,
        name: values.name,
        children: editingOrg?.children || []
      };

      if (editingOrg?.id) {
        // 编辑模式
        const updateOrgRecursive = (orgs: Organization[]): Organization[] => {
          return orgs.map(org => {
            if (org.id === editingOrg.id) {
              return newOrg;
            }
            if (org.children) {
              return { ...org, children: updateOrgRecursive(org.children) };
            }
            return org;
          });
        };
        setOrganizations(updateOrgRecursive([...organizations]));
        message.success('编辑成功');
      } else {
        // 新增模式
        if (values.parentId) {
          // 添加子节点
          const addChildRecursive = (orgs: Organization[]): Organization[] => {
            return orgs.map(org => {
              if (org.id === values.parentId) {
                return {
                  ...org,
                  children: [...(org.children || []), newOrg]
                };
              }
              if (org.children) {
                return { ...org, children: addChildRecursive(org.children) };
              }
              return org;
            });
          };
          setOrganizations(addChildRecursive([...organizations]));
        } else {
          // 添加根节点
          setOrganizations([...organizations, newOrg]);
        }
        message.success('新增成功');
      }
      
      setIsModalOpen(false);
      form.resetFields();
    } catch (error) {
      console.error('Validation failed:', error);
    }
  };

  return (
    <div className="h-full flex flex-col">
      {/* 头部 */}
      <div className="p-4 border-b border-gray-200">
        <h2 className="text-lg mb-3">组织架构</h2>
        <Space direction="vertical" className="w-full" size="middle">
          <Search
            placeholder="搜索组织名称"
            prefix={<SearchOutlined />}
            allowClear
            value={searchValue}
            onChange={(e) => setSearchValue(e.target.value)}
          />
          <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={handleAddRoot}
            className="w-full"
          >
            新增根组织
          </Button>
        </Space>
      </div>

      {/* 树形列表 */}
      <div className="flex-1 overflow-auto p-4">
        <Tree
          showIcon
          showLine
          selectedKeys={[selectedOrgId]}
          expandedKeys={expandedKeys}
          onExpand={(keys) => setExpandedKeys(keys as string[])}
          onSelect={(keys) => {
            if (keys.length > 0) {
              onSelectOrg(keys[0] as string);
            }
          }}
          treeData={getFilteredTreeData}
        />
      </div>

      {/* 新增/编辑弹窗 */}
      <Modal
        title={editingOrg?.id ? '编辑组织' : '新增组织'}
        open={isModalOpen}
        onOk={handleSave}
        onCancel={() => {
          setIsModalOpen(false);
          form.resetFields();
        }}
        okText="确定"
        cancelText="取消"
      >
        <Form form={form} layout="vertical" className="mt-4">
          <Form.Item name="parentName" label="父组织">
            <Input disabled placeholder="根组织" />
          </Form.Item>
          <Form.Item
            name="code"
            label="组织编码"
            rules={[{ required: true, message: '请输入组织编码' }]}
          >
            <Input placeholder="请输入组织编码" />
          </Form.Item>
          <Form.Item
            name="name"
            label="组织名称"
            rules={[{ required: true, message: '请输入组织名称' }]}
          >
            <Input placeholder="请输入组织名称" />
          </Form.Item>
          <Form.Item name="parentId" hidden>
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}

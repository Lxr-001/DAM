import React, { useEffect } from 'react';
import { Drawer, Form, Input, Select, DatePicker, Button, Space } from 'antd';
import dayjs from 'dayjs';
import type { User } from '../App';

interface UserDrawerProps {
  visible: boolean;
  onClose: () => void;
  onSave: (user: User) => void;
  user: User | null;
}

const statusOptions = [
  { label: '正常', value: '正常' },
  { label: '锁定', value: '锁定' },
  { label: '停用', value: '停用' }
];

const roleOptions = [
  { label: '数据管理员', value: '数据管理员' },
  { label: '数据空间管理员', value: '数据空间管理员' },
  { label: '数据Owner', value: '数据Owner' },
  { label: '数据管家', value: '数据管家' },
  { label: '数据使用者', value: '数据使用者' }
];

const securityLevelOptions = [
  { label: '绝密', value: '绝密' },
  { label: '机密', value: '机密' },
  { label: '内部', value: '内部' },
  { label: '公开', value: '公开' }
];

export default function UserDrawer({ visible, onClose, onSave, user }: UserDrawerProps) {
  const [form] = Form.useForm();

  useEffect(() => {
    if (visible) {
      if (user) {
        // 编辑模式 - 填充表单
        form.setFieldsValue({
          ...user,
          effectiveDate: user.effectiveDate ? dayjs(user.effectiveDate) : null,
          expiryDate: user.expiryDate ? dayjs(user.expiryDate) : null
        });
      } else {
        // 新增模式 - 设置默认值
        form.setFieldsValue({
          status: '正常',
          securityLevel: '公开',
          roles: []
        });
      }
    } else {
      // 关闭时重置表单
      form.resetFields();
    }
  }, [visible, user, form]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      const userData: User = {
        id: user?.id || '',
        username: values.username,
        position: values.position,
        status: values.status,
        roles: values.roles,
        securityLevel: values.securityLevel,
        effectiveDate: values.effectiveDate ? values.effectiveDate.format('YYYY-MM-DD') : '',
        expiryDate: values.expiryDate ? values.expiryDate.format('YYYY-MM-DD') : '',
        orgId: user?.orgId || ''
      };
      onSave(userData);
    } catch (error) {
      console.error('Validation failed:', error);
    }
  };

  return (
    <Drawer
      title={user ? '编辑用户' : '新增用户'}
      placement="right"
      width={520}
      onClose={onClose}
      open={visible}
      extra={
        <Space>
          <Button onClick={onClose}>取消</Button>
          <Button type="primary" onClick={handleSubmit}>
            保存
          </Button>
        </Space>
      }
    >
      <Form
        form={form}
        layout="vertical"
        requiredMark="optional"
      >
        <Form.Item
          name="username"
          label="用户名"
          rules={[{ required: true, message: '请输入用户名' }]}
        >
          <Input placeholder="请输入用户名" />
        </Form.Item>

        <Form.Item
          name="position"
          label="岗位/职务"
          rules={[{ required: true, message: '请输入岗位/职务' }]}
        >
          <Input placeholder="请输入岗位/职务" />
        </Form.Item>

        <Form.Item
          name="status"
          label="状态"
          rules={[{ required: true, message: '请选择状态' }]}
        >
          <Select
            placeholder="请选择状态"
            options={statusOptions}
          />
        </Form.Item>

        <Form.Item
          name="roles"
          label="角色"
          rules={[{ required: true, message: '请选择角色' }]}
        >
          <Select
            mode="multiple"
            placeholder="请选择角色（可多选）"
            options={roleOptions}
          />
        </Form.Item>

        <Form.Item
          name="securityLevel"
          label="安全等级"
          rules={[{ required: true, message: '请选择安全等级' }]}
        >
          <Select
            placeholder="请选择安全等级"
            options={securityLevelOptions}
          />
        </Form.Item>

        <Form.Item
          name="effectiveDate"
          label="权限生效日期"
          rules={[{ required: true, message: '请选择权限生效日期' }]}
        >
          <DatePicker
            placeholder="请选择日期"
            format="YYYY-MM-DD"
            className="w-full"
          />
        </Form.Item>

        <Form.Item
          name="expiryDate"
          label="权限过期日期"
          rules={[{ required: true, message: '请选择权限过期日期' }]}
        >
          <DatePicker
            placeholder="请选择日期"
            format="YYYY-MM-DD"
            className="w-full"
          />
        </Form.Item>
      </Form>
    </Drawer>
  );
}

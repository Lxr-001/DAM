import { useState } from "react";
import { useNavigate, useParams } from "react-router";
import { ArrowLeft, Upload, FolderPlus, Shield, Settings, HardDrive, FileText, Users as UsersIcon } from "lucide-react";
import { Button } from "./ui/button";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "./ui/tabs";
import { Progress } from "./ui/progress";
import { Sidebar } from "./Sidebar";
import { TopBar } from "./TopBar";

export function DataSpaceDetail() {
  const navigate = useNavigate();
  const { id } = useParams();
  const [activeMenu, setActiveMenu] = useState("all-spaces");
  const [activeTab, setActiveTab] = useState("overview");

  // Mock space data
  const space = {
    id: id || "DS-2024-001",
    name: "客户数据中心",
    description: "存储所有客户相关的数据资产，包括客户信息、订单记录等",
    type: "department",
    usedSpace: 234,
    totalSpace: 500,
    fileCount: 1247,
    memberCount: 24,
    lastUpdated: "2024-03-08 14:30",
  };

  const usagePercent = (space.usedSpace / space.totalSpace) * 100;
  const isWarning = usagePercent > 80;
  const isDanger = usagePercent > 95;

  const files = [
    { id: 1, name: "客户信息表.xlsx", size: "2.3MB", type: "file", updatedAt: "2024-03-08 10:20" },
    { id: 2, name: "订单数据", size: "120MB", type: "folder", updatedAt: "2024-03-07 15:45" },
    { id: 3, name: "分析报告.pdf", size: "5.6MB", type: "file", updatedAt: "2024-03-06 09:12" },
    { id: 4, name: "图片资源", size: "45MB", type: "folder", updatedAt: "2024-03-05 16:30" },
  ];

  const members = [
    { id: 1, name: "张三", role: "管理员", email: "zhangsan@company.com", joinedAt: "2024-01-15" },
    { id: 2, name: "李四", role: "编辑", email: "lisi@company.com", joinedAt: "2024-02-01" },
    { id: 3, name: "王五", role: "只读", email: "wangwu@company.com", joinedAt: "2024-02-15" },
  ];

  return (
    <div className="flex h-screen bg-gray-50">
      <Sidebar activeMenu={activeMenu} onMenuChange={setActiveMenu} />
      <div className="flex-1 flex flex-col overflow-hidden">
        <TopBar />
        
        <div className="flex-1 overflow-auto">
          <div className="p-6">
            <div className="mb-6">
              <Button
                variant="ghost"
                size="sm"
                onClick={() => navigate("/")}
                className="mb-4"
              >
                <ArrowLeft className="w-4 h-4 mr-2" />
                返回列表
              </Button>
              
              <div className="flex items-start justify-between">
                <div>
                  <h1 className="text-2xl font-semibold text-gray-900 mb-2">{space.name}</h1>
                  <p className="text-gray-600">{space.description}</p>
                  <p className="text-sm text-gray-500 mt-1">空间ID: {space.id}</p>
                </div>
                <Button variant="outline" size="sm">
                  <Settings className="w-4 h-4 mr-2" />
                  空间设置
                </Button>
              </div>
            </div>

            <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
              <TabsList className="bg-white border-b border-gray-200 w-full justify-start rounded-none p-0 h-auto">
                <TabsTrigger
                  value="overview"
                  className="rounded-none border-b-2 border-transparent data-[state=active]:border-blue-600 data-[state=active]:bg-transparent px-6 py-3"
                >
                  概览
                </TabsTrigger>
                <TabsTrigger
                  value="files"
                  className="rounded-none border-b-2 border-transparent data-[state=active]:border-blue-600 data-[state=active]:bg-transparent px-6 py-3"
                >
                  文件管理
                </TabsTrigger>
                <TabsTrigger
                  value="members"
                  className="rounded-none border-b-2 border-transparent data-[state=active]:border-blue-600 data-[state=active]:bg-transparent px-6 py-3"
                >
                  成员与权限
                </TabsTrigger>
                <TabsTrigger
                  value="settings"
                  className="rounded-none border-b-2 border-transparent data-[state=active]:border-blue-600 data-[state=active]:bg-transparent px-6 py-3"
                >
                  空间设置
                </TabsTrigger>
              </TabsList>

              <TabsContent value="overview" className="mt-6">
                <div className="grid grid-cols-3 gap-6 mb-6">
                  <div className="bg-white rounded-lg border border-gray-200 p-6">
                    <div className="flex items-center gap-3 mb-4">
                      <div className="p-2 bg-blue-100 rounded-lg">
                        <HardDrive className="w-6 h-6 text-blue-600" />
                      </div>
                      <h3 className="font-medium text-gray-900">存储容量</h3>
                    </div>
                    <div className="space-y-2">
                      <div className="flex items-end gap-2">
                        <span className="text-3xl font-semibold text-gray-900">{space.usedSpace}</span>
                        <span className="text-gray-500 mb-1">/ {space.totalSpace}GB</span>
                      </div>
                      <Progress
                        value={usagePercent}
                        className="h-2"
                        indicatorClassName={isDanger ? "bg-red-500" : isWarning ? "bg-yellow-500" : "bg-blue-500"}
                      />
                      <p className="text-sm text-gray-500">已使用 {usagePercent.toFixed(1)}%</p>
                    </div>
                  </div>

                  <div className="bg-white rounded-lg border border-gray-200 p-6">
                    <div className="flex items-center gap-3 mb-4">
                      <div className="p-2 bg-green-100 rounded-lg">
                        <FileText className="w-6 h-6 text-green-600" />
                      </div>
                      <h3 className="font-medium text-gray-900">文件数量</h3>
                    </div>
                    <div className="flex items-end gap-2">
                      <span className="text-3xl font-semibold text-gray-900">{space.fileCount.toLocaleString()}</span>
                      <span className="text-gray-500 mb-1">个文件</span>
                    </div>
                  </div>

                  <div className="bg-white rounded-lg border border-gray-200 p-6">
                    <div className="flex items-center gap-3 mb-4">
                      <div className="p-2 bg-purple-100 rounded-lg">
                        <UsersIcon className="w-6 h-6 text-purple-600" />
                      </div>
                      <h3 className="font-medium text-gray-900">成员数量</h3>
                    </div>
                    <div className="flex items-end gap-2">
                      <span className="text-3xl font-semibold text-gray-900">{space.memberCount}</span>
                      <span className="text-gray-500 mb-1">位成员</span>
                    </div>
                  </div>
                </div>

                <div className="bg-white rounded-lg border border-gray-200 p-6">
                  <div className="flex items-center justify-between mb-4">
                    <h3 className="font-semibold text-gray-900">快速操作</h3>
                    <span className="text-sm text-gray-500">最近更新：{space.lastUpdated}</span>
                  </div>
                  <div className="grid grid-cols-3 gap-4">
                    <Button className="h-24 flex flex-col gap-2">
                      <Upload className="w-6 h-6" />
                      <span>上传文件</span>
                    </Button>
                    <Button variant="outline" className="h-24 flex flex-col gap-2">
                      <FolderPlus className="w-6 h-6" />
                      <span>新建文件夹</span>
                    </Button>
                    <Button variant="outline" className="h-24 flex flex-col gap-2">
                      <Shield className="w-6 h-6" />
                      <span>权限设置</span>
                    </Button>
                  </div>
                </div>
              </TabsContent>

              <TabsContent value="files" className="mt-6">
                <div className="bg-white rounded-lg border border-gray-200">
                  <div className="p-4 border-b border-gray-200 flex items-center justify-between">
                    <h3 className="font-semibold text-gray-900">文件列表</h3>
                    <div className="flex gap-2">
                      <Button size="sm">
                        <Upload className="w-4 h-4 mr-2" />
                        上传
                      </Button>
                      <Button size="sm" variant="outline">
                        <FolderPlus className="w-4 h-4 mr-2" />
                        新建文件夹
                      </Button>
                    </div>
                  </div>
                  <div className="divide-y divide-gray-200">
                    {files.map((file) => (
                      <div key={file.id} className="p-4 hover:bg-gray-50 flex items-center justify-between">
                        <div className="flex items-center gap-3">
                          <div className={`p-2 rounded-lg ${file.type === "folder" ? "bg-yellow-100" : "bg-blue-100"}`}>
                            {file.type === "folder" ? (
                              <FileText className="w-5 h-5 text-yellow-600" />
                            ) : (
                              <FileText className="w-5 h-5 text-blue-600" />
                            )}
                          </div>
                          <div>
                            <p className="font-medium text-gray-900">{file.name}</p>
                            <p className="text-sm text-gray-500">{file.size}</p>
                          </div>
                        </div>
                        <div className="flex items-center gap-4">
                          <span className="text-sm text-gray-500">{file.updatedAt}</span>
                          <Button variant="ghost" size="sm">操作</Button>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              </TabsContent>

              <TabsContent value="members" className="mt-6">
                <div className="bg-white rounded-lg border border-gray-200">
                  <div className="p-4 border-b border-gray-200 flex items-center justify-between">
                    <h3 className="font-semibold text-gray-900">成员列表</h3>
                    <Button size="sm">
                      <UsersIcon className="w-4 h-4 mr-2" />
                      添加成员
                    </Button>
                  </div>
                  <table className="w-full">
                    <thead className="bg-gray-50 border-b border-gray-200">
                      <tr>
                        <th className="text-left text-xs font-medium text-gray-700 px-4 py-3">姓名</th>
                        <th className="text-left text-xs font-medium text-gray-700 px-4 py-3">邮箱</th>
                        <th className="text-left text-xs font-medium text-gray-700 px-4 py-3">角色</th>
                        <th className="text-left text-xs font-medium text-gray-700 px-4 py-3">加入时间</th>
                        <th className="text-left text-xs font-medium text-gray-700 px-4 py-3">操作</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-200">
                      {members.map((member) => (
                        <tr key={member.id} className="hover:bg-gray-50">
                          <td className="px-4 py-3 text-gray-900">{member.name}</td>
                          <td className="px-4 py-3 text-gray-600">{member.email}</td>
                          <td className="px-4 py-3">
                            <span className="px-2 py-1 bg-blue-100 text-blue-700 rounded text-xs">
                              {member.role}
                            </span>
                          </td>
                          <td className="px-4 py-3 text-gray-600">{member.joinedAt}</td>
                          <td className="px-4 py-3">
                            <Button variant="ghost" size="sm">编辑</Button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </TabsContent>

              <TabsContent value="settings" className="mt-6">
                <div className="bg-white rounded-lg border border-gray-200 p-6">
                  <h3 className="font-semibold text-gray-900 mb-4">空间设置</h3>
                  <div className="space-y-6">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">空间名称</label>
                      <input
                        type="text"
                        defaultValue={space.name}
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">空间描述</label>
                      <textarea
                        defaultValue={space.description}
                        rows={3}
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg"
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">容量配额</label>
                      <div className="flex gap-2">
                        <input
                          type="number"
                          defaultValue={space.totalSpace}
                          className="w-32 px-3 py-2 border border-gray-300 rounded-lg"
                        />
                        <span className="flex items-center text-gray-600">GB</span>
                        <Button variant="outline" size="sm">申请扩容</Button>
                      </div>
                    </div>
                    <div className="pt-4 border-t border-gray-200">
                      <Button>保存设置</Button>
                    </div>
                  </div>
                </div>
              </TabsContent>
            </Tabs>
          </div>
        </div>
      </div>
    </div>
  );
}

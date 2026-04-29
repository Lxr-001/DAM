import { useState } from "react";
import { Sidebar } from "./Sidebar";
import { TopBar } from "./TopBar";
import { FilterBar } from "./FilterBar";
import { SpaceCard } from "./SpaceCard";
import { Button } from "./ui/button";
import { Plus, Download, Upload, Grid3x3, List, Trash2 } from "lucide-react";
import { useNavigate } from "react-router";
import { Checkbox } from "./ui/checkbox";

export function DataSpaceList() {
  const navigate = useNavigate();
  const [activeMenu, setActiveMenu] = useState("all-spaces");
  const [viewMode, setViewMode] = useState<"grid" | "list">("grid");
  const [selectedSpaces, setSelectedSpaces] = useState<string[]>([]);
  const [filters, setFilters] = useState({
    type: "all",
    status: "all",
    quickFilter: "",
  });

  const handleFilterChange = (key: string, value: string) => {
    setFilters((prev) => ({ ...prev, [key]: value }));
  };

  const handleSpaceSelect = (id: string) => {
    navigate(`/space/${id}`);
  };

  const toggleSpaceSelection = (id: string) => {
    setSelectedSpaces((prev) =>
      prev.includes(id) ? prev.filter((spaceId) => spaceId !== id) : [...prev, id]
    );
  };

  // Mock data
  const spaces = [
    {
      id: "DS-2024-001",
      name: "客户数据中心",
      description: "存储所有客户相关的数据资产，包括客户信息、订单记录等",
      type: "department",
      usedSpace: 234,
      totalSpace: 500,
      creator: "张三",
      createdAt: "2024-01-15",
      memberCount: 24,
      permission: "admin",
      isFavorite: true,
    },
    {
      id: "DS-2024-002",
      name: "AI训练数据集",
      description: "用于机器学习模型训练的数据集合",
      type: "project",
      usedSpace: 480,
      totalSpace: 500,
      creator: "李四",
      createdAt: "2024-02-20",
      memberCount: 12,
      permission: "editor",
      expiryDate: "2024-12-31",
    },
    {
      id: "DS-2024-003",
      name: "财务数据归档",
      description: "历史财务数据的长期归档存储",
      type: "private",
      usedSpace: 120,
      totalSpace: 1000,
      creator: "王五",
      createdAt: "2024-01-10",
      memberCount: 5,
      permission: "viewer",
    },
    {
      id: "DS-2024-004",
      name: "产品数据库",
      description: "产品相关的所有数据和文档资料",
      type: "public",
      usedSpace: 340,
      totalSpace: 500,
      creator: "赵六",
      createdAt: "2024-03-01",
      memberCount: 45,
      permission: "admin",
      isFavorite: true,
    },
    {
      id: "DS-2024-005",
      name: "市场分析报告",
      description: "市场调研和分析报告数据",
      type: "department",
      usedSpace: 78,
      totalSpace: 200,
      creator: "孙七",
      createdAt: "2024-02-15",
      memberCount: 18,
      permission: "editor",
    },
    {
      id: "DS-2024-006",
      name: "研发测试环境",
      description: "研发团队测试数据和日志",
      type: "project",
      usedSpace: 195,
      totalSpace: 200,
      creator: "周八",
      createdAt: "2024-03-05",
      memberCount: 32,
      permission: "admin",
      expiryDate: "2024-06-30",
    },
  ];

  return (
    <div className="flex h-screen bg-gray-50">
      <Sidebar activeMenu={activeMenu} onMenuChange={setActiveMenu} />
      <div className="flex-1 flex flex-col overflow-hidden">
        <TopBar />
        <FilterBar filters={filters} onFilterChange={handleFilterChange} />
        
        <div className="flex-1 overflow-auto">
          <div className="p-6">
            <div className="flex items-center justify-between mb-6">
              <div className="flex items-center gap-3">
                <h3 className="text-lg font-semibold text-gray-900">
                  全部空间 <span className="text-gray-500 font-normal">({spaces.length})</span>
                </h3>
                {selectedSpaces.length > 0 && (
                  <span className="text-sm text-gray-600">
                    已选择 {selectedSpaces.length} 个空间
                  </span>
                )}
              </div>
              <div className="flex items-center gap-2">
                {selectedSpaces.length > 0 && (
                  <>
                    <Button variant="outline" size="sm">
                      批量授权
                    </Button>
                    <Button variant="outline" size="sm">
                      批量转移
                    </Button>
                    <Button variant="outline" size="sm" className="text-red-600">
                      <Trash2 className="w-4 h-4 mr-1" />
                      批量删除
                    </Button>
                    <div className="w-px h-6 bg-gray-300 mx-1"></div>
                  </>
                )}
                <Button variant="outline" size="sm">
                  <Upload className="w-4 h-4 mr-1" />
                  导入
                </Button>
                <Button variant="outline" size="sm">
                  <Download className="w-4 h-4 mr-1" />
                  导出
                </Button>
                <div className="flex border border-gray-300 rounded-lg">
                  <button
                    onClick={() => setViewMode("grid")}
                    className={`p-2 ${
                      viewMode === "grid"
                        ? "bg-blue-50 text-blue-600"
                        : "text-gray-600 hover:bg-gray-50"
                    }`}
                  >
                    <Grid3x3 className="w-4 h-4" />
                  </button>
                  <button
                    onClick={() => setViewMode("list")}
                    className={`p-2 ${
                      viewMode === "list"
                        ? "bg-blue-50 text-blue-600"
                        : "text-gray-600 hover:bg-gray-50"
                    }`}
                  >
                    <List className="w-4 h-4" />
                  </button>
                </div>
                <Button className="bg-blue-600 hover:bg-blue-700">
                  <Plus className="w-4 h-4 mr-1" />
                  新建数据空间
                </Button>
              </div>
            </div>

            {viewMode === "grid" ? (
              <div className="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-5">
                {spaces.map((space) => (
                  <div key={space.id} className="relative">
                    <div className="absolute top-2 left-2 z-10">
                      <Checkbox
                        checked={selectedSpaces.includes(space.id)}
                        onCheckedChange={() => toggleSpaceSelection(space.id)}
                        className="bg-white border-2"
                      />
                    </div>
                    <SpaceCard space={space} onSelect={handleSpaceSelect} />
                  </div>
                ))}
              </div>
            ) : (
              <div className="bg-white rounded-lg border border-gray-200 overflow-hidden">
                <table className="w-full">
                  <thead className="bg-gray-50 border-b border-gray-200">
                    <tr>
                      <th className="w-12 px-4 py-3">
                        <Checkbox />
                      </th>
                      <th className="text-left text-xs font-medium text-gray-700 px-4 py-3">空间名称</th>
                      <th className="text-left text-xs font-medium text-gray-700 px-4 py-3">空间ID</th>
                      <th className="text-left text-xs font-medium text-gray-700 px-4 py-3">类型</th>
                      <th className="text-left text-xs font-medium text-gray-700 px-4 py-3">容量使用</th>
                      <th className="text-left text-xs font-medium text-gray-700 px-4 py-3">成员数</th>
                      <th className="text-left text-xs font-medium text-gray-700 px-4 py-3">创建人</th>
                      <th className="text-left text-xs font-medium text-gray-700 px-4 py-3">权限</th>
                      <th className="text-left text-xs font-medium text-gray-700 px-4 py-3">操作</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-200">
                    {spaces.map((space) => {
                      const usagePercent = (space.usedSpace / space.totalSpace) * 100;
                      const isDanger = usagePercent > 95;
                      const isWarning = usagePercent > 80;
                      
                      return (
                        <tr key={space.id} className="hover:bg-gray-50">
                          <td className="px-4 py-3">
                            <Checkbox
                              checked={selectedSpaces.includes(space.id)}
                              onCheckedChange={() => toggleSpaceSelection(space.id)}
                            />
                          </td>
                          <td className="px-4 py-3">
                            <button
                              onClick={() => handleSpaceSelect(space.id)}
                              className="font-medium text-gray-900 hover:text-blue-600"
                            >
                              {space.name}
                            </button>
                          </td>
                          <td className="px-4 py-3 text-sm text-gray-600">{space.id}</td>
                          <td className="px-4 py-3">
                            <span className="px-2 py-1 bg-blue-100 text-blue-700 rounded text-xs">
                              {space.type}
                            </span>
                          </td>
                          <td className="px-4 py-3">
                            <div className="text-sm">
                              <span className={isDanger ? "text-red-600" : isWarning ? "text-yellow-600" : "text-gray-900"}>
                                {space.usedSpace}/{space.totalSpace}GB
                              </span>
                            </div>
                          </td>
                          <td className="px-4 py-3 text-sm text-gray-600">{space.memberCount}</td>
                          <td className="px-4 py-3 text-sm text-gray-600">{space.creator}</td>
                          <td className="px-4 py-3">
                            <span className="px-2 py-1 bg-gray-100 text-gray-700 rounded text-xs">
                              {space.permission}
                            </span>
                          </td>
                          <td className="px-4 py-3">
                            <Button variant="ghost" size="sm">
                              详情
                            </Button>
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

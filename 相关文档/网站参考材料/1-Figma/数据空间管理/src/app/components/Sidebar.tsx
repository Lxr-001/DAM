import { Database, Folder, Users, Trash2, CheckSquare, Shield } from "lucide-react";
import { cn } from "../lib/utils";

interface SidebarProps {
  activeMenu: string;
  onMenuChange: (menu: string) => void;
}

export function Sidebar({ activeMenu, onMenuChange }: SidebarProps) {
  const menuItems = [
    { id: "overview", icon: Database, label: "数据空间总览" },
    { id: "my-spaces", icon: Folder, label: "我的空间" },
    { id: "all-spaces", icon: Users, label: "全部空间" },
    { id: "recycle", icon: Trash2, label: "空间回收站" },
    { id: "approval", icon: CheckSquare, label: "空间审批/申请" },
    { id: "permissions", icon: Shield, label: "数据权限管理" },
  ];

  return (
    <div className="w-56 bg-white border-r border-gray-200 flex flex-col h-screen">
      <div className="p-4 border-b border-gray-200">
        <h1 className="font-semibold text-lg text-gray-900">数据平台</h1>
      </div>
      <nav className="flex-1 p-3">
        {menuItems.map((item) => {
          const Icon = item.icon;
          return (
            <button
              key={item.id}
              onClick={() => onMenuChange(item.id)}
              className={cn(
                "w-full flex items-center gap-3 px-3 py-2.5 rounded-lg mb-1 transition-colors",
                activeMenu === item.id
                  ? "bg-blue-50 text-blue-600"
                  : "text-gray-700 hover:bg-gray-50"
              )}
            >
              <Icon className="w-5 h-5" />
              <span className="text-sm">{item.label}</span>
            </button>
          );
        })}
      </nav>
    </div>
  );
}

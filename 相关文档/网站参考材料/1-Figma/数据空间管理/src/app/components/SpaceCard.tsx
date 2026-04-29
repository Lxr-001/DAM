import { MoreVertical, Users, Calendar, HardDrive, Star } from "lucide-react";
import { Progress } from "./ui/progress";
import { Button } from "./ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "./ui/dropdown-menu";
import { cn } from "../lib/utils";

interface SpaceCardProps {
  space: {
    id: string;
    name: string;
    description: string;
    type: string;
    usedSpace: number;
    totalSpace: number;
    creator: string;
    createdAt: string;
    memberCount: number;
    permission: string;
    expiryDate?: string;
    isFavorite?: boolean;
  };
  onSelect: (id: string) => void;
}

export function SpaceCard({ space, onSelect }: SpaceCardProps) {
  const usagePercent = (space.usedSpace / space.totalSpace) * 100;
  const isWarning = usagePercent > 80;
  const isDanger = usagePercent > 95;

  const typeLabels: Record<string, { label: string; color: string }> = {
    private: { label: "私有", color: "bg-purple-100 text-purple-700" },
    public: { label: "公共", color: "bg-green-100 text-green-700" },
    project: { label: "项目", color: "bg-blue-100 text-blue-700" },
    department: { label: "部门", color: "bg-orange-100 text-orange-700" },
  };

  const permissionLabels: Record<string, string> = {
    admin: "管理员",
    editor: "编辑",
    viewer: "只读",
  };

  return (
    <div className="bg-white rounded-lg border border-gray-200 p-5 hover:shadow-md transition-shadow">
      <div className="flex items-start justify-between mb-3">
        <div className="flex-1">
          <div className="flex items-center gap-2 mb-2">
            <button
              onClick={() => onSelect(space.id)}
              className="font-semibold text-lg text-gray-900 hover:text-blue-600 transition-colors"
            >
              {space.name}
            </button>
            <span className={cn("px-2 py-0.5 rounded text-xs font-medium", typeLabels[space.type]?.color)}>
              {typeLabels[space.type]?.label}
            </span>
            {space.isFavorite && <Star className="w-4 h-4 text-yellow-500 fill-yellow-500" />}
          </div>
          <p className="text-sm text-gray-500 mb-1">ID: {space.id}</p>
          <p className="text-sm text-gray-600">{space.description}</p>
        </div>
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" size="sm" className="h-8 w-8 p-0">
              <MoreVertical className="w-4 h-4" />
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            <DropdownMenuItem onClick={() => onSelect(space.id)}>查看详情</DropdownMenuItem>
            <DropdownMenuItem>编辑</DropdownMenuItem>
            <DropdownMenuItem>权限管理</DropdownMenuItem>
            <DropdownMenuItem>转移</DropdownMenuItem>
            <DropdownMenuItem className="text-red-600">删除</DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>

      <div className="space-y-3 mt-4">
        <div>
          <div className="flex items-center justify-between text-sm mb-1.5">
            <span className="text-gray-600">容量使用</span>
            <span className={cn(
              "font-medium",
              isDanger ? "text-red-600" : isWarning ? "text-yellow-600" : "text-gray-900"
            )}>
              {space.usedSpace}GB / {space.totalSpace}GB
            </span>
          </div>
          <Progress
            value={usagePercent}
            className="h-2"
            indicatorClassName={cn(
              isDanger ? "bg-red-500" : isWarning ? "bg-yellow-500" : "bg-blue-500"
            )}
          />
        </div>

        <div className="flex items-center justify-between text-sm pt-3 border-t border-gray-100">
          <div className="flex items-center gap-4">
            <div className="flex items-center gap-1.5 text-gray-600">
              <Users className="w-4 h-4" />
              <span>{space.memberCount} 成员</span>
            </div>
            <div className="flex items-center gap-1.5 text-gray-600">
              <Calendar className="w-4 h-4" />
              <span>{space.createdAt}</span>
            </div>
          </div>
          <span className="px-2 py-1 bg-gray-100 text-gray-700 rounded text-xs font-medium">
            {permissionLabels[space.permission]}
          </span>
        </div>

        <div className="flex items-center justify-between text-xs text-gray-500 pt-2">
          <span>创建人：{space.creator}</span>
          {space.expiryDate && (
            <span className="text-orange-600">过期时间：{space.expiryDate}</span>
          )}
        </div>
      </div>
    </div>
  );
}

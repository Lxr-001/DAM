import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "./ui/select";
import { Button } from "./ui/button";
import { Filter } from "lucide-react";

interface FilterBarProps {
  filters: {
    type: string;
    status: string;
    quickFilter: string;
  };
  onFilterChange: (key: string, value: string) => void;
}

export function FilterBar({ filters, onFilterChange }: FilterBarProps) {
  return (
    <div className="bg-white border-b border-gray-200 p-4">
      <div className="flex items-center gap-3">
        <Filter className="w-4 h-4 text-gray-500" />
        <span className="text-sm font-medium text-gray-700">筛选条件：</span>
        
        <Select value={filters.type} onValueChange={(value) => onFilterChange("type", value)}>
          <SelectTrigger className="w-40">
            <SelectValue placeholder="空间类型" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">全部类型</SelectItem>
            <SelectItem value="private">私有</SelectItem>
            <SelectItem value="public">公共</SelectItem>
            <SelectItem value="project">项目</SelectItem>
            <SelectItem value="department">部门</SelectItem>
          </SelectContent>
        </Select>

        <Select value={filters.status} onValueChange={(value) => onFilterChange("status", value)}>
          <SelectTrigger className="w-40">
            <SelectValue placeholder="状态" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">全部状态</SelectItem>
            <SelectItem value="normal">正常</SelectItem>
            <SelectItem value="full">已满</SelectItem>
            <SelectItem value="expiring">即将过期</SelectItem>
          </SelectContent>
        </Select>

        <div className="flex-1"></div>

        <div className="flex gap-2">
          <Button
            variant={filters.quickFilter === "managed" ? "default" : "outline"}
            size="sm"
            onClick={() => onFilterChange("quickFilter", filters.quickFilter === "managed" ? "" : "managed")}
          >
            我管理的
          </Button>
          <Button
            variant={filters.quickFilter === "participated" ? "default" : "outline"}
            size="sm"
            onClick={() => onFilterChange("quickFilter", filters.quickFilter === "participated" ? "" : "participated")}
          >
            我参与的
          </Button>
          <Button
            variant={filters.quickFilter === "favorite" ? "default" : "outline"}
            size="sm"
            onClick={() => onFilterChange("quickFilter", filters.quickFilter === "favorite" ? "" : "favorite")}
          >
            我收藏的
          </Button>
        </div>
      </div>
    </div>
  );
}

import { createBrowserRouter } from "react-router";
import { DataSpaceList } from "./components/DataSpaceList";
import { DataSpaceDetail } from "./components/DataSpaceDetail";

export const router = createBrowserRouter([
  {
    path: "/",
    Component: DataSpaceList,
  },
  {
    path: "/space/:id",
    Component: DataSpaceDetail,
  },
]);

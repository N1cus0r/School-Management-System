import CourseDetailsBar from "../navigation/CourseDetailsBar.tsx";
import { Outlet } from "react-router-dom";

const CourseDetailsLayout = () => {
  return (
    <CourseDetailsBar>
      <Outlet />
    </CourseDetailsBar>
  );
};
export default CourseDetailsLayout;

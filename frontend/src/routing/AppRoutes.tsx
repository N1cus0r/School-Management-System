import { Route, Routes } from "react-router-dom";
import Layout from "../components/shared/Layout.tsx";
import LoginPage from "../pages/LoginPage.tsx";
import PrivateRoute from "./PrivateRoute.tsx";
import GradesPage from "../pages/GradesPage.tsx";
import HomeworksPage from "../pages/HomeworksPage.tsx";
import AttendancesPage from "../pages/AttendancesPage.tsx";
import CommentsPage from "../pages/CommentsPage.tsx";
import NotFoundPage from "../pages/NotFoundPage.tsx";
import { Role } from "../entities/user/Role.ts";
import UnauthorizedPage from "../pages/UnauthorizedPage.tsx";
import UsersPage from "../pages/UsersPage.tsx";
import CoursesPage from "../pages/CoursesPage.tsx";
import CourseDetailsLayout from "../components/course/CourseDetailsLayout.tsx";
import CourseHomeworksPage from "../pages/CourseHomeworksPage.tsx";
import CourseGradesPage from "../pages/CourseGradesPage.tsx";
import CourseAttendancesPage from "../pages/CourseAttendancesPage.tsx";
import CourseCommentsPage from "../pages/CourseCommentsPage.tsx";

const AppRoutes = () => {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route element={<PrivateRoute allowedRoles={[Role.STUDENT]} />}>
          <Route path="/grades" element={<GradesPage />} />
          <Route path="/homeworks" element={<HomeworksPage />} />
          <Route path="/attendances" element={<AttendancesPage />} />
          <Route path="/comments" element={<CommentsPage />} />
        </Route>
        <Route element={<PrivateRoute allowedRoles={[Role.ADMIN]} />}>
          <Route
            path="/teachers"
            element={<UsersPage usersRole={Role.TEACHER} />}
          />
        </Route>
        <Route
          element={<PrivateRoute allowedRoles={[Role.ADMIN, Role.TEACHER]} />}
        >
          <Route
            path="/students"
            element={<UsersPage usersRole={Role.STUDENT} />}
          />
          <Route path="/courses" element={<CoursesPage />} />
          <Route element={<CourseDetailsLayout />}>
            <Route
              path={"/courses/:courseId/homeworks"}
              element={<CourseHomeworksPage />}
            />
            <Route
              path={"/courses/:courseId/grades"}
              element={<CourseGradesPage />}
            />
            <Route
              path={"/courses/:courseId/attendances"}
              element={<CourseAttendancesPage />}
            />
            <Route
              path={"/courses/:courseId/comments"}
              element={<CourseCommentsPage />}
            />
          </Route>
        </Route>
        <Route path="/unauthorized" element={<UnauthorizedPage />} />
        <Route path="*" element={<NotFoundPage />} />
      </Route>
      <Route path="/login" element={<LoginPage />} />
    </Routes>
  );
};
export default AppRoutes;

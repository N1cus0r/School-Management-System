import {Select} from "@chakra-ui/react";
import {ChangeEvent} from "react";
import useTeachers from "../../hooks/user/useTeachers.ts";
import CreateCourseFormPlaceholder from "./CreateCourseFormPlaceholder.tsx";

interface Props {
  value: number;
  onChange: (e: ChangeEvent<HTMLSelectElement>) => void;
}

const CourseTeacherSelect = ({ value, onChange }: Props) => {
  const { data: teachers } = useTeachers();

  if (!teachers || teachers.pages[0].length === 0)
    return <CreateCourseFormPlaceholder />;

  return (
    <Select
      placeholder="Choose a Teacher"
      name="teacherId"
      value={value}
      onChange={onChange}
    >
      {teachers.pages.map((page) =>
        page.map((teacher) => (
          <option key={teacher.id} value={teacher.id}>
            {teacher.fullName}
          </option>
        )),
      )}
    </Select>
  );
};
export default CourseTeacherSelect;

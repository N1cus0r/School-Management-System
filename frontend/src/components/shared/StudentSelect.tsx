import {Select} from "@chakra-ui/react";
import CourseEntityFormPlaceholder from "../course/CourseEntityFormPlaceholder.tsx";
import useStudents from "../../hooks/user/useStudents.ts";
import {ChangeEvent} from "react";
import {User} from "../../entities/user/User.ts";

type Page = User[]
interface UseStudentsQueryResult {
  data?: {
    pages: Page[];
  };
}
interface Props {
  value?: number;
  onChange: (e: ChangeEvent<HTMLSelectElement>) => void;
  hook?: () => UseStudentsQueryResult;
}
const StudentSelect = ({ value, onChange, hook }: Props) => {
  const { data: students } = hook ? hook() : useStudents();

  if (!students || students.pages[0].length === 0)
    return <CourseEntityFormPlaceholder />;

  return (
    <Select
      placeholder="Choose a Student"
      name="studentId"
      value={value}
      onChange={onChange}
    >
      {students.pages.map((page) =>
        page.map((student) => (
          <option key={student.id} value={student.id}>
            {student.fullName}
          </option>
        )),
      )}
    </Select>
  );
};
export default StudentSelect;

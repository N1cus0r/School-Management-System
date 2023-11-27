import {Button, FormControl, FormLabel, Stack} from "@chakra-ui/react";
import {FormikProps} from "formik";
import StudentSelect from "../shared/StudentSelect.tsx";
import {OperationType} from "../../entities/OperationType.ts";
import useCourseStudents from "../../hooks/user/useCourseStudents.ts";
import useCourseNonParticipatingStudents from "../../hooks/user/useCourseNonParticipatingStudents.ts";

interface Props {
  operationType: OperationType;
  courseId: number;
  form: FormikProps<{
    studentId: number;
  }>;
}

const CourseStudentForm = ({
  operationType,
  courseId,
  form,
}: Props) => {
  const studentChoicesHook =
    operationType === OperationType.ADD
      ? () => useCourseNonParticipatingStudents(courseId)
      : () => useCourseStudents(courseId);

  return (
    <Stack spacing={4}>
      <FormControl>
        <FormLabel>Student</FormLabel>
        <StudentSelect
          value={form.values.studentId}
          onChange={form.handleChange}
          hook={studentChoicesHook}
        />
      </FormControl>
      <Button
        colorScheme={"blue"}
        variant={"solid"}
        isDisabled={!form.dirty || !form.isValid}
        onClick={form.handleSubmit}
      >
          {operationType === OperationType.ADD ? "Add" : "Remove"}
      </Button>
    </Stack>
  );
};
export default CourseStudentForm;

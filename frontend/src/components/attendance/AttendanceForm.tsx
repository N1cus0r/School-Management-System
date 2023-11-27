import { FormikProps } from "formik";
import { Button, FormControl, FormLabel, Stack, Text } from "@chakra-ui/react";
import CourseStudentSelect from "../shared/CourseStudentSelect.tsx";
import { AttendanceType } from "../../entities/attendance/AttendanceType.ts";
import { AttendancePeriod } from "../../entities/attendance/AttendancePeriod.ts";
import { FormType } from "../../entities/FormType.ts";
import AttendanceTypeSelect from "./AttendanceTypeSelect.tsx";
import AttendancePeriodSelect from "./AttendancePeriodSelect.tsx";

interface Props {
  formType: FormType;
  form: FormikProps<{
    type: AttendanceType;
    period: AttendancePeriod;
    studentId: number;
  }>;
  submitButtonText: string;
}
const AttendanceForm = ({ formType, form, submitButtonText }: Props) => {
  return (
    <Stack spacing={4}>
      <FormControl id="type">
        <FormLabel>Type</FormLabel>
        <AttendanceTypeSelect
          value={form.values.type}
          onChange={form.handleChange}
        />
        {form.touched.type && form.errors.type && (
          <Text color="red.500" fontSize="sm" mt={1}>
            {form.errors.type}
          </Text>
        )}
      </FormControl>
      <FormControl id="period">
        <FormLabel>Period</FormLabel>
        <AttendancePeriodSelect
          value={form.values.period}
          onChange={form.handleChange}
        />
        {form.touched.period && form.errors.period && (
          <Text color="red.500" fontSize="sm" mt={1}>
            {form.errors.period}
          </Text>
        )}
      </FormControl>
      {formType === FormType.CREATE && (
        <FormControl>
          <FormLabel>Student</FormLabel>
          <CourseStudentSelect
            value={form.values.studentId}
            onChange={form.handleChange}
          />
        </FormControl>
      )}
      <Button
        colorScheme={"blue"}
        variant={"solid"}
        isDisabled={!form.dirty || !form.isValid}
        onClick={form.handleSubmit}
      >
        {submitButtonText}
      </Button>
    </Stack>
  );
};
export default AttendanceForm;

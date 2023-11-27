import {FormikProps} from "formik";
import {Button, FormControl, FormLabel, Input, Stack, Text,} from "@chakra-ui/react";
import GradeValueSelect from "./GradeValueSelect.tsx";
import CourseStudentSelect from "../shared/CourseStudentSelect.tsx";
import {FormType} from "../../entities/FormType.ts";

interface Props {
  formType: FormType
  form: FormikProps<{ value: number; text: string; studentId: number }>;
  submitButtonText: string;
}
const GradeForm = ({ formType, form, submitButtonText }: Props) => {
  return (
    <Stack spacing={4}>
      <FormControl id="grade">
        <FormLabel>Grade</FormLabel>
        <GradeValueSelect
          value={form.values.value}
          onChange={form.handleChange}
        />
        {form.touched.value && form.errors.value && (
          <Text color="red.500" fontSize="sm" mt={1}>
            {form.errors.value}
          </Text>
        )}
      </FormControl>
      <FormControl id="text">
        <FormLabel>Description</FormLabel>
        <Input
          type="text"
          name="text"
          placeholder="Grade Description"
          value={form.values.text}
          onChange={form.handleChange}
        />
        {form.touched.text && form.errors.text && (
          <Text color="red.500" fontSize="sm" mt={1}>
            {form.errors.text}
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
export default GradeForm;

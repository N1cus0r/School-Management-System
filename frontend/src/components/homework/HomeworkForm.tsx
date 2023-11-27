import {
  Button,
  FormControl,
  FormLabel,
  Input,
  Stack,
  Text,
  Textarea,
} from "@chakra-ui/react";
import { FormikProps } from "formik";

interface Props {
  form: FormikProps<{ text: string; dueDate: string }>;
  submitButtonText: string
}

const HomeworkForm = ({ form, submitButtonText }: Props) => {
  return (
    <Stack spacing={4}>
      <FormControl id="text">
        <FormLabel>Text</FormLabel>
        <Textarea
          name="text"
          placeholder="Homework Description"
          rows={6}
          resize="none"
          value={form.values.text}
          onChange={form.handleChange}
        />
        {form.touched.text && form.errors.text && (
          <Text color="red.500" fontSize="sm" mt={1}>
            {form.errors.text}
          </Text>
        )}
      </FormControl>
      <FormControl id="dateOfBirth">
        <FormLabel>Due Date</FormLabel>
        <Input
          type="date"
          name="dueDate"
          value={form.values.dueDate}
          onChange={form.handleChange}
        />
        {form.touched.dueDate && form.errors.dueDate && (
          <Text color="red.500" fontSize="sm" mt={1}>
            {form.errors.dueDate}
          </Text>
        )}
      </FormControl>
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
export default HomeworkForm;

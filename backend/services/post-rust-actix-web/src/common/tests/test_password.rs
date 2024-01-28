use crate::common::Password;

#[test]
fn test_empty_password() {
    assert_eq!(Password::try_from_plain("".to_string()), None);
}

#[test]
fn test_short_password() {
    assert_eq!(Password::try_from_plain("a".to_string()), None);
}

#[test]
fn test_long_password() {
    assert_eq!(Password::try_from_plain("a".repeat(73)), None);
}

#[test]
fn test_proper_password() {
    assert!(Password::try_from_plain("iF@35p".to_string()).is_some());
}

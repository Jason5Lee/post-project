use crate::common::Time;

pub struct Snowflake {
    machine_id: u16,
    idx: u16,
    last_time_stamp: u64,
}

impl Snowflake {
    pub fn new(machine_id: u16) -> Self {
        if machine_id >= 1024 {
            panic!("machine_id  must be between 0 and 1023")
        }
        Self {
            machine_id,
            idx: 0,
            last_time_stamp: 0,
        }
    }
    fn get_id(&self) -> u64 {
        (self.last_time_stamp << 22 | ((self.machine_id << 12) as u64) | (self.idx as u64))
            & (i64::MAX as u64) // Make highest bit 0
    }

    pub fn generate_id(&mut self) -> super::Result<(u64, Time)> {
        let now = super::current_timestamp_utc();
        if now <= self.last_time_stamp {
            if self.idx == 4096 {
                Err(crate::common::api::overloaded())
            } else {
                let id = self.get_id();
                self.idx += 1;
                Ok((id, Time { utc: now }))
            }
        } else {
            self.last_time_stamp = now;
            self.idx = 0;
            Ok((self.get_id(), Time { utc: now }))
        }
    }
}

pub const ID_DUPLICATE_MESSAGE: &str =
    "ID duplicated. Possible cause: some nodes have the same NODE_ID.";
